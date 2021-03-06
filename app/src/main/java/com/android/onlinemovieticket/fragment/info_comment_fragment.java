package com.android.onlinemovieticket.fragment;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.CommentAdapter;
import com.android.onlinemovieticket.adapter.onRecyclerItemClickListener;
import com.android.onlinemovieticket.db.Comment;
import com.android.onlinemovieticket.repository.CommentRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.UserRepository;
import com.android.onlinemovieticket.z_smallactivity.Info_Comment;
import com.bumptech.glide.Glide;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class info_comment_fragment extends Fragment {
    private Button btn_all;
    private Button btn_my;
    private ImageButton btn_suggest;
    private ImageButton btn_comment;
    private ProgressBar progressBar;

    private List<Comment> allCommentList = new ArrayList<>();
    private List<Comment> showCommentList = new ArrayList<>();
    private CommentAdapter adapter;
    private RecyclerView commentView;

    private int mid;
    private int scnum;
    private int scall;
    private String account;
    private String type;
    private boolean isbuy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_comment_fragment, container, false);

        account = getArguments().getString("account");
        mid = getArguments().getInt("mid");
        type = getArguments().getString("type");
        isbuy = getArguments().getBoolean("isbuy");
        scnum = getArguments().getInt("scnum");
        scall = getArguments().getInt("scall");

        btn_all = view.findViewById(R.id.comment_fragment_button_all);
        btn_my = view.findViewById(R.id.comment_fragment_button_my);
        btn_suggest = view.findViewById(R.id.comment_fragment_button_suggest);
        btn_comment = view.findViewById(R.id.comment_fragment_button_comment);
        progressBar = view.findViewById(R.id.comment_fragment_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        commentView = view.findViewById(R.id.comment_fragment_listview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentView.setLayoutManager(layoutManager);
        commentView.addItemDecoration(new DividerItemDecoration(getContext()
                ,DividerItemDecoration.VERTICAL));

        if (isbuy) {
            alert_edit(true);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadComment();

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_all.setTextColor(Color.parseColor("#FF4081"));
                btn_my.setTextColor(Color.parseColor("#000000"));
                progressBar.setVisibility(View.VISIBLE);
                loadComment_all();
            }
        });

        btn_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_my.setTextColor(Color.parseColor("#FF4081"));
                btn_all.setTextColor(Color.parseColor("#000000"));
                loadComment_my();
            }
        });

        btn_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_suggest();
            }
        });

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_edit(false);
            }
        });
    }

    private void alert_suggest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("????????????");
        TextView textView = new TextView(getContext());
        textView.setText("1?????????????????????????????????????????????????????????\n" +
                "2????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                "3???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                "4????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                "5????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n");
        builder.setView(textView);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void alert_edit(boolean isGrade) {

        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        List<Integer> grade = new ArrayList<>(1);

        if (isGrade) {
            LinearLayout layout = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(params);

            RatingBar ratingBar = new RatingBar(getContext());
            layout.addView(ratingBar);
            ll.addView(layout);

            grade.add(ratingBar.getProgress());
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    int rating1 = ratingBar.getProgress();
                    Toast.makeText(getContext(), "????????????: " + rating1,
                            Toast.LENGTH_SHORT).show();
                    grade.clear();
                    grade.add(rating1);
                }
            });
        } else {
            grade.add(-1);
        }
        EditText et = new EditText(getContext());
        ll.addView(et);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("?????????");
        builder.setView(ll);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBar.setVisibility(View.VISIBLE);
                submitComment(et.getText().toString(), grade.get(0), -1);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void submitComment(String content, int grade, int other_comment_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 2;
                CommentRepository commentRepository = new CommentRepository();
                UserRepository userRepository = new UserRepository();
                Comment comment = new Comment(mid, userRepository.
                        getImgByAccount(account), account, grade, content, getNowDate(),
                        0, "", other_comment_id);

                MovieRepository movieRepository = new MovieRepository();
                movieRepository.updateMsc(scall + grade,
                        scnum + 1, mid);
                int result = commentRepository.addComment(comment);
                if (result == 1) {
                    msg = 3;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    public void delConfirm(int comment_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("????????????");
        builder.setMessage("???????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                delComment(comment_id);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void delComment(int comment_id) {
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

    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    private void updateGood_num(int comment_id, int good_num, String account) {
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

    private void loadComment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 0;
                CommentRepository commentRepository = new CommentRepository();
                allCommentList = commentRepository.getCommentByMid(mid);
                if (allCommentList.size() > 0) {
                    msg = 1;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    private void loadComment_all() {
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
                Intent intent = new Intent(getActivity(), Info_Comment.class);
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

    private void loadComment_my() {
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
                Intent intent = new Intent(getActivity(), Info_Comment.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comment", comment);
                intent.putExtras(bundle);
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

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(getContext(), "????????????????????????????????????",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                Toast.makeText(getContext(), "??????????????????",
                        Toast.LENGTH_SHORT).show();
                loadComment_all();
            } else if (msg.what == 2) {
                Toast.makeText(getContext(), "????????????????????????????????????",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                Toast.makeText(getContext(), "??????????????????",
                        Toast.LENGTH_SHORT).show();
                loadComment();
            } else if (msg.what == 4) {
                Toast.makeText(getContext(), "??????????????????????????????",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 5) {
                Toast.makeText(getContext(), "????????????",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 6) {
                Toast.makeText(getContext(), "????????????????????????????????????",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 7) {
                Toast.makeText(getContext(), "??????????????????",
                        Toast.LENGTH_SHORT).show();
                loadComment();
            }
            progressBar.setVisibility(View.GONE);
        }
    };
}