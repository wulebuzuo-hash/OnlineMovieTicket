package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.fragment.info_comment_fragment;
import com.android.onlinemovieticket.fragment.info_movie_fragment;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.utils.ImageViewAnimationHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class List_Comment extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ImageView movie_View;
    private TextView movie_long;
    private TextView movie_screen;
    private TextView movie_type;
    private TextView movie_date;

    private RadioGroup radioGroup;
    private RadioButton btn_message;
    private RadioButton btn_comment;

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

        radioGroup = findViewById(R.id.list_comment_radiogroup);
        radioGroup.check(R.id.list_comment_btn_message);
        btn_message = findViewById(R.id.list_comment_btn_message);
        setBounds(R.drawable.pc_movie_introduce, btn_message);
        btn_message.setOnClickListener(this);
        btn_comment = findViewById(R.id.list_comment_btn_comment);
        setBounds(R.drawable.pc_comment2, btn_comment);
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
                manager.beginTransaction().hide(comment_fragment).show(movie_fragment).commit();
                break;
            case R.id.list_comment_btn_comment:
                manager.beginTransaction().hide(movie_fragment).show(comment_fragment).commit();
                break;

        }
    }

    /**
     * @param drawableId  drawableLeft  drawableTop drawableBottom 所用的选择器 通过R.drawable.xx 获得
     * @param radioButton 需要限定图片大小的radioButton
     */
    private void setBounds(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形  (这里的长和宽写死了 自己可以可以修改成 形参传入)
        drawable_news.setBounds(0, 0, 70, 70);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(drawable_news, null, null, null);
    }

    /**
     * 显示碎片movie_fragment
     */
    private void add_movie_fragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("mid", movie.getMid());
        bundle.putString("account", account);
        bundle.putString("type", type);
        bundle.putDouble("ticket_price", ticket_price);

        DecimalFormat f = new DecimalFormat("#0.0");
        double rating = movie.getMscall() * 1.0 / movie.getMscnum();
        bundle.putString("sc", f.format(rating) + "分");
        bundle.putFloat("rating", (float) rating);
        bundle.putString("scnum", movie.getMscnum() + "人评价");
        bundle.putString("pf", movie.getMpf() + "元");
        bundle.putString("story", movie.getMstory());
        bundle.putString("director", movie.getMdir());
        bundle.putString("actor", movie.getMactor());
        movie_fragment.setArguments(bundle);
        transaction.add(R.id.list_comment_frame, movie_fragment, "movie_fragment").commit();
    }

    /**
     * 显示碎片comment_fragment
     */
    private void add_comment_fragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("mid", mid);
        bundle.putString("account", account);
        bundle.putString("type", type);
        bundle.putBoolean("isbuy", isbuy);
        bundle.putInt("scnum", movie.getMscnum());
        bundle.putInt("scall", movie.getMscall());
        comment_fragment.setArguments(bundle);
        transaction.add(R.id.list_comment_frame, comment_fragment).commit();
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
                    movie_date.setText(showdate + " 上映");

                    manager = getSupportFragmentManager();  //获取FragmentManager
                    add_movie_fragment();
                    add_comment_fragment();
                    manager.beginTransaction().hide(comment_fragment).show(movie_fragment).commit();
                }
            }
        }
    };
}