package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
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
    private ImageView imageView;
    private Button comment_all;
    private Button comment_my;
    private FloatingActionButton btn_comment;
    private ProgressBar progressBar;
    private List<Comment> allCommentList = new ArrayList<>();
    private List<Comment> showCommentList = new ArrayList<>();
    private CommentAdapter adapter;
    private RecyclerView commentView;

    private Movie movie;
    private int mid;
    private String account;
    private String type;
    private boolean isbuy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_comment);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        isbuy = getIntent().getBooleanExtra("isbuy", false);

        mid = getIntent().getIntExtra("mid", 0);
        loadMovie();
        toolbar = findViewById(R.id.list_comment_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById(R.id.list_comment_collapsing_toolbar);
        imageView = findViewById(R.id.list_comment_movie_image);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        commentView = findViewById(R.id.list_comment_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentView.setLayoutManager(layoutManager);

        progressBar = findViewById(R.id.list_comment_progressbar);
        progressBar.setVisibility(View.VISIBLE);


        if (isbuy) {
            alert_edit(true);
        }

        comment_all = findViewById(R.id.list_comment_button_all);
        comment_all.setOnClickListener(this);
        comment_my = findViewById(R.id.list_comment_button_my);
        comment_my.setOnClickListener(this);
        btn_comment = findViewById(R.id.list_comment_fab);
        btn_comment.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_comment_button_all:
                comment_all.setTextColor(Color.parseColor("#FF4081"));
                comment_my.setTextColor(Color.parseColor("#000000"));
                progressBar.setVisibility(View.VISIBLE);
                initComment_all();
                break;
            case R.id.list_comment_button_my:
                comment_my.setTextColor(Color.parseColor("#FF4081"));
                comment_all.setTextColor(Color.parseColor("#000000"));
                progressBar.setVisibility(View.VISIBLE);
                initComment_my();
                break;
            case R.id.list_comment_fab:
                alert_edit(false);
                break;

        }
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
                CommentRepository commentRepository = new CommentRepository();
                allCommentList = commentRepository.getCommentByMid(movie.getMid());
                if (allCommentList.size() != 0) {
                    msg = 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (movie != null) {

                                byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
                                Glide.with(List_Comment.this).load(BitmapFactory.decodeByteArray(
                                        imageBytes, 0, imageBytes.length)).into(imageView);
                                collapsingToolbarLayout.setTitle(movie.getMname());
                            }
                            progressBar.setVisibility(View.GONE);
                            initComment_all();
                        }
                    });
                }

                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    public void alert_edit(boolean isGrade) {

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        List<Integer> grade = new ArrayList<>(1);

        if (isGrade) {
            LinearLayout layout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(params);

            RatingBar ratingBar = new RatingBar(this);
            layout.addView(ratingBar);
            ll.addView(layout);

            grade.add(ratingBar.getProgress());
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    int rating1 = ratingBar.getProgress();
                    Toast.makeText(List_Comment.this, "当前打分: " + rating1,
                            Toast.LENGTH_SHORT).show();
                    grade.clear();
                    grade.add(rating1);
                }
            });
        } else {
            grade.add(-1);
        }
        EditText et = new EditText(this);
        ll.addView(et);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");
        builder.setView(ll);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                submitComment(et.getText().toString(), grade.get(0), -1);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void initComment_all() {
        showCommentList.clear();
        for (Comment comment : allCommentList) {
            if (comment.getSc() != -1) {
                showCommentList.add(comment);
            }
        }
        adapter = new CommentAdapter(account, showCommentList);
        adapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isClick) {
                Comment comment = showCommentList.get(position);
                if (isClick) {
                    updateGood_num(comment.getComment_id(), comment.getGood_num() + 1,
                            comment.getGood_user_id() + "," + account);
                } else {
                    updateGood_num(comment.getComment_id(), comment.getGood_num() - 1,
                            comment.getGood_user_id().replace("," + account, ""));
                }
            }

            @Override
            public void onItemClick(int position) {
                Comment comment = showCommentList.get(position);
                Intent intent = new Intent(List_Comment.this, Info_Comment.class);
                intent.putExtra("account", account);
                intent.putExtra("comment_id", comment.getComment_id());
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {
                Comment comment = showCommentList.get(position);
                if (comment.getUaccount().equals(account)) {
                    delConfirm(comment.getComment_id());
                }
            }
        });
        commentView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    public void initComment_my() {
        showCommentList.clear();
        for (Comment comment : allCommentList) {
            if (comment.getUaccount().equals(account)) {
                showCommentList.add(comment);
            }
        }
        adapter = new CommentAdapter(account, showCommentList);
        adapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isClick) {
                Comment comment = showCommentList.get(position);
                if (isClick) {
                    updateGood_num(comment.getComment_id(), comment.getGood_num() + 1,
                            comment.getGood_user_id() + "," + account);
                } else {
                    updateGood_num(comment.getComment_id(), comment.getGood_num() - 1,
                            comment.getGood_user_id().replace("," + account, ""));
                }
            }

            @Override
            public void onItemClick(int position) {
                Comment comment = showCommentList.get(position);
                Intent intent = new Intent(List_Comment.this, Info_Comment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comment", comment);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }

            ;

            @Override
            public void onLongClick(int position) {
                Comment comment = showCommentList.get(position);
                if (comment.getUaccount().equals(account)) {
                    delConfirm(comment.getComment_id());
                }
            }
        });
        commentView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    public void submitComment(String content, int grade, int other_comment_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 2;
                CommentRepository commentRepository = new CommentRepository();
                UserRepository userRepository = new UserRepository();
                Comment comment = new Comment(movie.getMid(), userRepository.
                        getImgByAccount(account), account, grade, content, getNowDate(),
                        0, "", other_comment_id);

                MovieRepository movieRepository = new MovieRepository();
                movieRepository.updateMsc(movie.getMscall() + grade,
                        movie.getMscnum() + 1, movie.getMid());
                int result = commentRepository.addComment(comment);
                if (result == 1) {
                    msg = 3;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    public void updateGood_num(int comment_id, int good_num, String account) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 4;
                CommentRepository commentRepository = new CommentRepository();
                int result = commentRepository.updateGoodNum(comment_id, good_num, account);
                if (result == 1) {
                    msg = 5;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    public void delConfirm(int comment_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除评论");
        builder.setMessage("确定删除该评论吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                delComment(comment_id);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void delComment(int comment_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 6;
                CommentRepository commentRepository = new CommentRepository();
                int result = commentRepository.deleteComment(comment_id);
                if (result == 1) {
                    msg = 7;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(List_Comment.this, "展示评论失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                Toast.makeText(List_Comment.this, "展示评论成功",
                        Toast.LENGTH_SHORT).show();
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
            progressBar.setVisibility(View.GONE);
        }
    };
}