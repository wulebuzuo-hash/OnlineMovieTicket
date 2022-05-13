package com.android.onlinemovieticket;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.fragment.Lay_bottom;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.z_smallactivity.Info_Cinema;
import com.android.onlinemovieticket.z_smallactivity.Info_Movie;
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
    private ImageButton addCinema;

    private EditText searchEdit;
    private Button searchButton;

    private CardView movie_card;
    private ImageView movie_img;
    private TextView movie_name;
    private TextView movie_long;
    private TextView movie_screen;
    private TextView movie_type;
    private TextView movie_date;
    private ProgressBar progressBar;

    private List<Cinema> cinemaList = new ArrayList<>();
    private List<Cinema> showCinemaList = new ArrayList<>();
    private List<Session> sessionList = new ArrayList<>();
    private MyAdapter adapter;
    private ListView cinemaView;

    private FragmentManager manager;
    private Fragment bottom_fragment;

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
        addCinema = (ImageButton) findViewById(R.id.title_button_add);

        searchEdit = (EditText) findViewById(R.id.m2_searchEdit);
        searchButton = (Button) findViewById(R.id.m2_searshButton);
        searchButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.m2_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        movie_card = (CardView) findViewById(R.id.m2_movie_card);
        movie_img = (ImageView) findViewById(R.id.m2_movie_img);
        movie_name = (TextView) findViewById(R.id.m2_movie_name);
        movie_long = (TextView) findViewById(R.id.m2_movie_long);
        movie_screen = (TextView) findViewById(R.id.m2_movie_screen);
        movie_type = (TextView) findViewById(R.id.m2_movie_type);
        movie_date = (TextView) findViewById(R.id.m2_movie_date);

        cinemaView = (ListView) findViewById(R.id.m2_cinemaView);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        mid = getIntent().getIntExtra("mid", 0);
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0.0);

        if (type.equals("BOSS")) {
            addCinema.setVisibility(View.VISIBLE);
            addCinema.setOnClickListener(this);
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
                Cinema cinema = showCinemaList.get(i);
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

        if(ticket_price == 0.0) {
            showBottom();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                if (movie != null) backConfirm();
                else loginConfirm();
                break;
            case R.id.m2_searshButton:
                String searsh = searchEdit.getText().toString();
                if (searsh.equals("")) {
                    Toast.makeText(CinemaActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchCinemas(searsh);
                }
                break;
            case R.id.title_button_add:
                addConfirm();
                break;
            default:
                break;
        }
    }

    private void showBottom(){
        manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);
        bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.m2_frame, bottom_fragment).commit();
    }

    /**
     * 加载电影，并调用initCinemas方法
     */
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

                            movie_name.setText(movie.getMname());
                            movie_long.setText(movie.getMlong() + "分钟");
                            movie_screen.setText(movie.getMscreen());
                            movie_type.setText(movie.getMtype());

                            DateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                            String showdate = sdf.format(movie.getShowdate());
                            movie_date.setText(showdate + " 上映");

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

                            initCinemas(movie.getMid());
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    /**
     * 根据mid获取正在上映该电影的影院列表
     */
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
                            if (getTime(currentDate, session.getShowDate()) >= 0 &&
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

    /**
     * 获取现在时间
     */
    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public long getTime(Date startTime, Date endTime) {
        long time = (endTime.getTime() - startTime.getTime()) / 1000;
        return time;
    }

    /**
     * 根据cinemaName搜索影院
     *
     * @param cinemaName
     */
    private void searchCinemas(String cinemaName) {
        List<Cinema> tempList = new ArrayList<>();
        for (Cinema cinema : showCinemaList) {
            if (cinema.getCname().contains(cinemaName)) {
                tempList.add(cinema);
            }
        }
        if (tempList.size() == 0) {
            Toast.makeText(CinemaActivity.this,
                    "没有找到相关影院", Toast.LENGTH_SHORT).show();
        } else {
            showCinemaList.clear();
            showCinemaList.addAll(tempList);
            adapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 返回电影首页确认
     */
    private void backConfirm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回首页？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CinemaActivity.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
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

    /**
     * 添加电影院确认
     */
    private void addConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加电影院？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CinemaActivity.this, Info_Cinema.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("cinemaList", (Serializable) cinemaList);
                startActivity(intent);
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

    /**
     * 返回登录页面确认
     */
    private void loginConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回登录页面？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(CinemaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    /**
     * 删除指定电影院确认
     * @param cid
     * @param name
     */
    private void delConfirm(int cid, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CinemaActivity.this);
        builder.setTitle("删除影院");
        builder.setMessage("确定删除影院{" + name + "}吗？");
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

    /**
     * 删除电影院操作
     * @param cid
     */
    private void delCinema(int cid) {
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

    /**
     * 初始化影院列表，listView适配器
     */
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

            Cinema cinema = showCinemaList.get(i);

            name.setText(cinema.getCname());
            position.setText(cinema.getCposition());
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