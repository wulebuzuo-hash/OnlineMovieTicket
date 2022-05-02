package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.CommentAdapter;
import com.android.onlinemovieticket.adapter.onRecyclerItemClickListener;
import com.android.onlinemovieticket.db.Comment;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.repository.CommentRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.UserRepository;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class List_Comment extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageView movie_View;
    private TextView movie_long;
    private TextView movie_screen;
    private TextView movie_type;
    private TextView movie_date;

    private Button btn_message;
    private Button btn_comment;

    private FragmentManager manager;
    private Fragment movie_fragment;
    private Fragment comment_fragment;

    private Movie movie;
    private int mid;
    private String account;
    private String type;
    private boolean isbuy;
    private double ticket_price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_comment);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        isbuy = getIntent().getBooleanExtra("isbuy", false);
        mid = getIntent().getIntExtra("mid", 0);
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0);

        toolbar = findViewById(R.id.list_comment_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.list_comment_collapsing_toolbar);
        movie_View = findViewById(R.id.list_comment_movie_image);
        movie_long = findViewById(R.id.list_comment_movie_long);
        movie_screen = findViewById(R.id.list_comment_movie_screen);
        movie_type = findViewById(R.id.list_comment_movie_type);
        movie_date = findViewById(R.id.list_comment_movie_date);
        btn_message = findViewById(R.id.list_comment_btn_message);
        btn_message.setOnClickListener(this);
        btn_comment = findViewById(R.id.list_comment_btn_comment);
        btn_comment.setOnClickListener(this);
        movie_fragment = new info_movie_fragment();
        comment_fragment = new info_comment_fragment();

        loadMovie();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_comment_btn_message:
                show_movie_fragment();
                break;
            case R.id.list_comment_btn_comment:
                show_comment_fragment();
                break;

        }
    }

    /**
     * 显示碎片movie_fragment
     */
    private void show_movie_fragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("mid", movie.getMid());
        bundle.putString("account", account);
        bundle.putString("type", type);
        bundle.putDouble("ticket_price", ticket_price);

        DecimalFormat f = new DecimalFormat("#0.0");
        double rating = movie.getMscall() * 1.0 / movie.getMscnum();
        bundle.putString("sc",f.format(rating) + "分");
        bundle.putFloat("rating", (float) rating);
        bundle.putString("scnum", movie.getMscnum() + "人评价");
        bundle.putString("pf", movie.getMpf()+"元");
        bundle.putString("story", movie.getMstory());
        bundle.putString("director", movie.getMdir());
        bundle.putString("actor", movie.getMactor());
        movie_fragment.setArguments(bundle);
        transaction.add(R.id.list_comment_frame, movie_fragment).commit();
    }

    /**
     * 显示碎片comment_fragment
     */
    private void show_comment_fragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("mid", mid);
        bundle.putString("account", account);
        bundle.putString("type", type);
        bundle.putBoolean("isbuy", isbuy);
        bundle.putInt("scnum", movie.getMscnum());
        bundle.putInt("scall", movie.getMscall());
        comment_fragment.setArguments(bundle);
        transaction.replace(R.id.list_comment_frame, comment_fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMovie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 0;
                MovieRepository movieRepository = new MovieRepository();
                movie = movieRepository.getMovieByMid(mid);
                if (movie != null) {
                    msg = 1;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(List_Comment.this, "展示失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                Toast.makeText(List_Comment.this, "展示成功",
                        Toast.LENGTH_SHORT).show();
                if (movie != null) {
                    byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
                    Glide.with(List_Comment.this).load(BitmapFactory.decodeByteArray(
                            imageBytes, 0, imageBytes.length)).into(movie_View);
                    collapsingToolbarLayout.setTitle(movie.getMname());
                    movie_long.setText(movie.getMlong() + "分钟");
                    movie_screen.setText(movie.getMscreen());
                    movie_type.setText(movie.getMtype());

                    DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                    String showdate = sdf.format(movie.getShowdate());
                    movie_date.setText(showdate+" 上映");

                    manager = getSupportFragmentManager();  //获取FragmentManager
                    show_movie_fragment();
                }
            } else if (msg.what == 2) {
                Toast.makeText(List_Comment.this, "提交评论失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                Toast.makeText(List_Comment.this, "提交评论成功",
                        Toast.LENGTH_SHORT).show();
                loadMovie();
            } else if (msg.what == 4) {
                Toast.makeText(List_Comment.this, "点赞失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 5) {
                Toast.makeText(List_Comment.this, "点赞成功",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {
                Toast.makeText(List_Comment.this, "删除评论失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 7) {
                Toast.makeText(List_Comment.this, "删除评论成功",
                        Toast.LENGTH_SHORT).show();
                loadMovie();
            }
        }
    };
}