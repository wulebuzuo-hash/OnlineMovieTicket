package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.CommentAdapter;
import com.android.onlinemovieticket.adapter.onRecyclerItemClickListener;
import com.android.onlinemovieticket.db.Comment;
import com.android.onlinemovieticket.repository.CommentRepository;
import com.android.onlinemovieticket.repository.UserRepository;
import com.bumptech.glide.Glide;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Info_Comment extends AppCompatActivity implements View.OnClickListener {
    private Button navButton;
    private TextView titleName;

    private ImageView user_image;
    private TextView user_account;
    private RatingBar ratingBar;
    private TextView comment_content;
    private TextView comment_time;
    private ImageView good_button;
    private TextView good_num;
    private ImageView comment_button;

    private ProgressBar progressBar;

    private RecyclerView comment_View;
    private CommentAdapter commentAdapter;

    private List<Comment> allCommentList;
    private List<Comment> showCommentList = new ArrayList<>();

    private Comment comment;
    private String account;
    private int comment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_comment);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("评论");

        user_image = (ImageView) findViewById(R.id.info_comment_user_image);
        user_account = (TextView) findViewById(R.id.info_comment_user_account);
        ratingBar = (RatingBar) findViewById(R.id.info_comment_rating);
        comment_content = (TextView) findViewById(R.id.info_comment_text);
        comment_time = (TextView) findViewById(R.id.info_comment_time);
        good_button = (ImageView) findViewById(R.id.info_comment_good_button);
        good_button.setOnClickListener(this);
        good_num = (TextView) findViewById(R.id.info_comment_good_num);
        comment_button = (ImageView) findViewById(R.id.info_comment_comment_button);
        comment_button.setOnClickListener(this);
        comment_View = (RecyclerView) findViewById(R.id.info_comment_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        comment_View.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) findViewById(R.id.info_comment_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        account = getIntent().getStringExtra("account");
        comment_id = getIntent().getIntExtra("comment_id", 0);
        if(comment_id != 0){
            loadComment();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.info_comment_good_button:
                progressBar.setVisibility(View.VISIBLE);
                if(comment.getGood_user_id().contains(account)) {
                    updateGood_num(comment.getComment_id(), comment.getGood_num() - 1,
                            comment.getGood_user_id().replace(","+account,""));
                }else {
                    updateGood_num(comment.getComment_id(), comment.getGood_num() + 1,
                            comment.getGood_user_id()+","+account);
                }
                break;
            case R.id.info_comment_comment_button:
                alert_edit();
                break;
            default:
                break;
        }
    }

    private void loadComment(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CommentRepository commentRepository = new CommentRepository();
                    comment = commentRepository.getCommentBycommentId(comment_id);
                    allCommentList = commentRepository.getCommentByMid(comment.getMid());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initComment();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void alert_edit() {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        EditText et = new EditText(this);
        ll.addView(et);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请输入");
        builder.setView(ll);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                submitComment(et.getText().toString(), comment.getComment_id());
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

    public void initComment() {
        if(comment.getGood_user_id() != null && comment.getGood_user_id().contains(account)) {
            good_button.setImageResource(R.drawable.ic_comment_good_1);
        }else {
            good_button.setImageResource(R.drawable.ic_comment_good_0);
        }

        byte[] imageBytes = Base64.decode(comment.getUser_image(), Base64.DEFAULT);
        Glide.with(this).load(BitmapFactory.
                decodeByteArray(imageBytes, 0, imageBytes.length)).into(user_image);

        user_account.setText(comment.getUaccount());

        int score = comment.getSc();
        ratingBar.setRating((float) score / 2);

        comment_content.setText(comment.getComment_text());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        comment_time.setText(dateFormat.format(comment.getComment_time()));
        good_num.setText(String.valueOf(comment.getGood_num()));

        for (int i = 0; i < allCommentList.size(); i++) {
            if (allCommentList.get(i).getOther_comment_id() == comment.getComment_id()) {
                showCommentList.add(allCommentList.get(i));
            }
        }

        commentAdapter = new CommentAdapter(account,showCommentList);
        commentAdapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isClick) {
                Comment comm = showCommentList.get(position);
                if (isClick) {
                    updateGood_num(comm.getComment_id(), comm.getGood_num() + 1,
                            comm.getGood_user_id()+","+account);
                } else {
                    updateGood_num(comm.getComment_id(), comm.getGood_num() - 1,
                            comm.getGood_user_id().replace(","+account,""));
                }
            }

            @Override
            public void onItemClick(int position) {
                Comment comment = showCommentList.get(position);
                Intent intent = new Intent(Info_Comment.this, Info_Comment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comment", comment);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onLongClick(int position) {
                Comment comment = showCommentList.get(position);
                if(comment.getUaccount().equals(account)){
                    delConfirm(comment.getComment_id());
                }
            }
        });
        comment_View.setAdapter(commentAdapter);
        progressBar.setVisibility(View.GONE);
    }

    public void submitComment(String content, int other_comment_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 2;
                CommentRepository commentRepository = new CommentRepository();
                UserRepository userRepository = new UserRepository();
                Comment comm= new Comment(comment.getMid(), userRepository.
                        getImgByAccount(account), account, -1, content, getNowDate(),
                        0, "", other_comment_id);

                int result = commentRepository.addComment(comment);
                if (result == 1) {
                    msg = 3;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            allCommentList.add(comm);
                            initComment();
                        }
                    });
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    public void updateGood_num(int comment_id, int good_num,String account) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 4;
                CommentRepository commentRepository = new CommentRepository();
                int result = commentRepository.updateGoodNum(comment_id, good_num,account);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initComment();
                        }
                    });
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
                Toast.makeText(Info_Comment.this, "展示评论失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 2) {
                Toast.makeText(Info_Comment.this, "提交评论失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 3) {
                Toast.makeText(Info_Comment.this, "提交评论成功",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                initComment();
            } else if (msg.what == 4) {
                Toast.makeText(Info_Comment.this, "点赞失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 5) {
                Toast.makeText(Info_Comment.this, "点赞成功",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 6) {
                Toast.makeText(Info_Comment.this, "删除评论失败，请检查网络",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 7) {
                Toast.makeText(Info_Comment.this, "删除评论成功",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                initComment();
            }
        }
    };
}