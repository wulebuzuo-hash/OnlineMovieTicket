package com.android.onlinemovieticket;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.z_smallactivity.Info_Cinema;
import com.android.onlinemovieticket.z_smallactivity.List_Admin;
import com.android.onlinemovieticket.z_smallactivity.List_Comment;
import com.android.onlinemovieticket.z_smallactivity.List_Session;
import com.android.onlinemovieticket.z_smallactivity.List_Uh;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CinemaActivity extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;

    private EditText searchEdit;
    private Button searchButton;

    private CardView movie_card;
    private ImageView movie_img;
    private TextView movie_sc;
    private TextView movie_scnum;
    private TextView movie_name;
    private TextView movie_long;
    private TextView movie_type;
    private TextView movie_pf;
    private TextView movie_date;
    private TextView movie_story;

    private Button addCinema;
    private ProgressBar progressBar;

    private List<Cinema> cinemaList = new ArrayList<>();
    private List<Cinema> showCinemaList = new ArrayList<>();
    private List<Session> sessionList = new ArrayList<>();
    private MyAdapter adapter;
    private ListView cinemaView;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private String account;
    private String type;
    private double ticket_price;
    private Movie movie;
    private int mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_2);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = (TextView) findViewById(R.id.title_name);
        titlename.setText("电影院");

        searchEdit = (EditText) findViewById(R.id.m2_searchEdit);
        searchButton = (Button) findViewById(R.id.m2_searshButton);
        searchButton.setOnClickListener(this);

        bottom_1 = (RadioButton) findViewById(R.id.bottom_choose_movie);
        bottom_1.setOnClickListener(this);
        bottom_2 = (RadioButton) findViewById(R.id.bottom_choose_cinema);
        bottom_2.setOnClickListener(this);
        bottom_3 = (RadioButton) findViewById(R.id.bottom_choose_my);
        bottom_3.setOnClickListener(this);

        addCinema = (Button) findViewById(R.id.m2_addCinema);
        addCinema.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.m2_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        movie_card = (CardView) findViewById(R.id.m2_movie_card);
        movie_img = (ImageView) findViewById(R.id.m2_movie_img);
        movie_sc = (TextView) findViewById(R.id.m2_movie_sc);
        movie_scnum = (TextView) findViewById(R.id.m2_movie_scnum);
        movie_name = (TextView) findViewById(R.id.m2_movie_name);
        movie_long = (TextView) findViewById(R.id.m2_movie_long);
        movie_type = (TextView) findViewById(R.id.m2_movie_type);
        movie_pf = (TextView) findViewById(R.id.m2_movie_pf);
        movie_date = (TextView) findViewById(R.id.m2_movie_date);
        movie_story = (TextView) findViewById(R.id.m2_movie_story);

        cinemaView = (ListView) findViewById(R.id.m2_cinemaView);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        mid = getIntent().getIntExtra("mid", 0);
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0.0);

        if (type.equals("BOSS")) {
            addCinema.setVisibility(View.VISIBLE);
            bottom_3.setText("管理员");
        } else {
            addCinema.setVisibility(View.GONE);
        }

        if (mid != 0) {
            progressBar.setVisibility(View.VISIBLE);
            loadMovie();
        } else {
            movie_card.setVisibility(View.GONE);
            initCinemas(-1);   //初始化电影院信息
        }


        cinemaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cinema cinema = cinemaList.get(i);
                Intent intent = null;
                if (type.equals("BOSS")) {
                    intent = new Intent(CinemaActivity.this, Info_Cinema.class);
                    intent.putExtra("cinemaList", (Serializable) cinemaList);
                } else if (type.equals("用户")) {
                    if (movie == null) {
                        intent = new Intent(CinemaActivity.this, MovieActivity.class);
                    } else {
                        intent = new Intent(CinemaActivity.this, List_Session.class);
                        intent.putExtra("ticket_price", ticket_price);
                        intent.putExtra("mid", mid);
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("cinema", cinema);
                intent.putExtras(bundle);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        cinemaView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cinema cinema = cinemaList.get(i);
                if (type.equals("BOSS")) {
                    delConfirm(cinema.getCid(), cinema.getCname());
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                Intent intent;
                if (movie != null) {
                    intent = new Intent(CinemaActivity.this, MovieActivity.class);
                    intent.putExtra("account", account);
                    intent.putExtra("type", type);
                } else {
                    intent = new Intent(CinemaActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
                break;
            case R.id.m2_searshButton:
                String searsh = searchEdit.getText().toString();
                if (searsh.equals("")) {
                    Toast.makeText(CinemaActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchCinemas(searsh);

                }
            case R.id.m2_addCinema:
                Intent intent1 = new Intent(CinemaActivity.this, Info_Cinema.class);
                intent1.putExtra("account", account);
                intent1.putExtra("type", type);
                intent1.putExtra("cinemaList", (Serializable) cinemaList);
                startActivity(intent1);
                break;
            case R.id.bottom_choose_movie:
                Intent intent2 = new Intent(CinemaActivity.this, MovieActivity.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);
                startActivity(intent2);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = new Intent(CinemaActivity.this, CinemaActivity.class);
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = null;
                if (type.equals("用户")) {
                    intent4 = new Intent(CinemaActivity.this, My_User.class);
                } else if (type.equals("管理员")) {
                    intent4 = new Intent(CinemaActivity.this, List_Uh.class);
                } else if (type.equals("BOSS")) {
                    intent4 = new Intent(CinemaActivity.this, List_Admin.class);
                }
                intent4.putExtra("account", account);
                intent4.putExtra("type", type);
                startActivity(intent4);
                finish();
                break;
            default:
                break;
        }
    }

    private void loadMovie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieRepository movieRepository = new MovieRepository();
                movie = movieRepository.getMovieByMid(mid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (movie != null) {
                            movie_card.setVisibility(View.VISIBLE);
                            byte[] image = Base64.decode(movie.getImgString(), Base64.DEFAULT);
                            Bitmap decodedImg = BitmapFactory.decodeByteArray(image, 0, image.length);
                            movie_img.setImageBitmap(decodedImg);

                            //按照四舍五入的方法保留一位小数
                            DecimalFormat f = new DecimalFormat("#0.0");
                            movie_sc.setText(f.format(movie.getMscall() * 1.0 / movie.getMscnum()) + "分");

                            movie_scnum.setText(movie.getMscnum() + "人参评");
                            movie_name.setText(movie.getMname());
                            movie_long.setText(movie.getMlong() + "分钟");
                            movie_type.setText(movie.getMtype());
                            movie_pf.setText(movie.getMpf() + "元");

                            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            String showdate = sdf.format(movie.getShowdate());
                            String downdate = sdf.format(movie.getDowndate());
                            movie_date.setText(showdate + "--" + downdate);

                            movie_story.setText(movie.getMstory());

                            initCinemas(movie.getMid());

                            movie_card.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CinemaActivity.this,
                                            List_Comment.class);
                                    intent.putExtra("account", account);
                                    intent.putExtra("type", type);
                                    intent.putExtra("mid", mid);
                                    startActivity(intent);
                                }
                            });
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    private void initCinemas(int mid) {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                CinemaRepository cinemaRepository = new CinemaRepository();
                cinemaList = cinemaRepository.findAllCinema();
                if (cinemaList.size() == 0) {
                    msg = 1;
                } else {
                    if (mid != -1) {
                        SessionRepository sessionRepository = new SessionRepository();
                        sessionList = sessionRepository.getSessionByMid(mid);
                        List<Integer> cidList = new ArrayList<>();
                        Date currentDate = getNowDate();
                        for (int i = 0; i < sessionList.size(); i++) {
                            Session session = sessionList.get(i);
                            if (getTime(currentDate,session.getShowDate()) >= 0 &&
                                    !cidList.contains(session.getCid())) {
                                cidList.add(session.getCid());
                            } else {
                                sessionList.remove(i);
                                i--;
                            }
                        }

                        for (Cinema cinema : cinemaList) {
                            if (cidList.contains(cinema.getCid())) {
                                showCinemaList.add(cinema);
                            }
                        }
                    } else {
                        showCinemaList = cinemaList;
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    //获取现在时间
    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    //计算时间差
    public long getTime(Date startTime, Date endTime) {
        long time = (endTime.getTime() - startTime.getTime()) / 1000;
        return time;
    }

    private void searchCinemas(String cinemaName) {
        List<Cinema> tempList = showCinemaList;
        showCinemaList.clear();
        for (Cinema cinema : tempList) {
            if (cinema.getCname().contains(cinemaName)) {
                showCinemaList.add(cinema);
            }
        }
        if (showCinemaList.size() == 0) {
            Toast.makeText(CinemaActivity.this,
                    "没有找到相关影院", Toast.LENGTH_SHORT).show();
        } else {
            adapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    public void delConfirm(int cid, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CinemaActivity.this);
        builder.setTitle("删除影院");
        builder.setMessage("确定删除影院" + name + "吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressBar.setVisibility(View.VISIBLE);
                delCinema(cid);
                initCinemas(-1);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public void delCinema(int cid) {
        new Thread() {
            @Override
            public void run() {
                CinemaRepository cinemaRepository = new CinemaRepository();
                boolean flag = cinemaRepository.delCinema(cid);
                if (flag) {
                    hand.sendEmptyMessage(4);
                } else {
                    hand.sendEmptyMessage(5);
                }
            }
        }.start();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showCinemaList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1;
            if (view == null) {    //打气筒 取得xml 里定义的view
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view1 = inflater.inflate(R.layout.item_cinema, null);
            } else view1 = view;

            TextView name = (TextView) view1.findViewById(R.id.cinema_item_name);
            TextView position = (TextView) view1.findViewById(R.id.cinema_item_position);
            ImageButton go = (ImageButton) view1.findViewById(R.id.cinema_item_map);

            Cinema cinema = showCinemaList.get(i);

            name.setText(cinema.getCname());
            position.setText(cinema.getCposition());

            go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                adapter = new MyAdapter();
                cinemaView.setAdapter(adapter);
            } else if (msg.what == 1) {
                Toast.makeText(CinemaActivity.this, "查询电影院失败！",
                        Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                Toast.makeText(CinemaActivity.this,
                        "搜索失败，请核对电影院名", Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(CinemaActivity.this,
                        "删除成功", Toast.LENGTH_LONG).show();
                initCinemas(-1);
            } else if (msg.what == 5) {
                Toast.makeText(CinemaActivity.this,
                        "删除失败", Toast.LENGTH_LONG).show();
            }
        }
    };
}