package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.SeatAdapter;
import com.android.onlinemovieticket.adapter.SessionAdapter;
import com.android.onlinemovieticket.adapter.onRecyclerItemClickListener;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class List_Session extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;

    private CardView cinema_card;
    private CardView movie_card;
    private TextView cinema_name;
    private TextView cinema_position;
    private TextView cinema_call;
    private ImageButton cinema_map;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private RecyclerView sessionDate;
    private List<String> dateList = new ArrayList<>();
    private List<Date> realDateList = new ArrayList<>();

    private TextView pftext;
    private ProgressBar progressBar;
    private List<Session> sessionList = new ArrayList<>();
    private List<Hall> hallList = new ArrayList<>();
    private List<Session> chooseSessionList = new ArrayList<>();
    private MyAdapter adapter;
    private ListView sessionView;
    private Button addSession;

    private Movie movie;
    private int mid;
    private Cinema cinema;
    private int cid;
    private String account;
    private String type;
    private double ticket_price;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_session);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = (TextView) findViewById(R.id.title_name);
        titlename.setText("场次");

        cinema_card = (CardView) findViewById(R.id.list_session_cinema_card);
        cinema_name = (TextView) findViewById(R.id.list_session_cinema_name);
        cinema_position = (TextView) findViewById(R.id.list_session_cinema_position);
        cinema_call = (TextView) findViewById(R.id.list_session_cinema_call);
        cinema_map = (ImageButton) findViewById(R.id.list_session_cinema_map);
        cinema_map.setOnClickListener(this);

        pftext = (TextView) findViewById(R.id.list_session_pf);

        movie_card = (CardView) findViewById(R.id.list_session_movie_card);

        bottom_1 = (RadioButton) findViewById(R.id.bottom_choose_movie);
        bottom_1.setOnClickListener(this);
        bottom_2 = (RadioButton) findViewById(R.id.bottom_choose_cinema);
        bottom_2.setOnClickListener(this);
        bottom_3 = (RadioButton) findViewById(R.id.bottom_choose_my);
        bottom_3.setOnClickListener(this);

        setBounds(R.drawable.pc_movie, bottom_1);
        setBounds(R.drawable.pc_cinema, bottom_2);
        setBounds(R.drawable.my, bottom_3);

        progressBar = (ProgressBar) findViewById(R.id.list_session_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        mid = getIntent().getIntExtra("mid", 0);
        cinema = (Cinema) getIntent().getSerializableExtra("cinema");
        cid = cinema == null ? getIntent().getIntExtra("cid", 0) : cinema.getCid();
        if (mid != 0) {
            progressBar.setVisibility(View.VISIBLE);
            loadMovie();
        }

        if (cinema != null && type.equals("用户")) {
            cinema_card.setVisibility(View.VISIBLE);
            movie_card.setVisibility(View.GONE);
            pftext.setVisibility(View.GONE);
            cinema_name.setText(cinema.getCname());
            cinema_position.setText(cinema.getCposition());
            cinema_call.setText(cinema.getCcall());
        }

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0.0);

        sessionDate = (RecyclerView) findViewById(R.id.list_session_date);
        sessionView = (ListView) findViewById(R.id.list_session_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        sessionDate.setLayoutManager(layoutManager);
        SessionAdapter sessionAdapter = new SessionAdapter(dateList);
        sessionAdapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                progressBar.setVisibility(View.VISIBLE);
                initSession(realDateList.get(position));
            }

            @Override
            public void onLongClick(int position) {
            }

            @Override
            public void onItemClick(int position, boolean isClick) {
            }
        });
        sessionDate.setAdapter(sessionAdapter);

        sessionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Session session = chooseSessionList.get(i);
                Intent intent = null;

                if (type.equals("用户")) {
                    intent = new Intent(List_Session.this, Choose_Seat.class);
                    intent.putExtra("ticket_price", ticket_price);
                    for (Hall hall : hallList) {
                        if (hall.getHid() == session.getHid()) {
                            intent.putExtra("hname", hall.getHname());
                            break;
                        }
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("session", session);
                    intent.putExtras(bundle);
                    intent.putExtra("movieName", movie.getMname());

                } else {
                    intent = new Intent(List_Session.this, Info_Session.class);
                    intent.putExtra("cid", cinema.getCid());
                    intent.putExtra("sessionList", (Serializable) sessionList);
                }

                intent.putExtra("mid", movie.getMid());
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });

        addSession = (Button) findViewById(R.id.list_session_add);
        addSession.setOnClickListener(this);

        if (type.equals("管理员")) {
            addSession.setVisibility(View.VISIBLE);
        } else {
            addSession.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_session_add:
                Intent intent = null;
                if (type.equals("管理员")) {
                    intent = new Intent(List_Session.this, Info_Session.class);
                    intent.putExtra("cid", cinema.getCid());
                    intent.putExtra("mid", movie.getMid());
                    intent.putExtra("mlong", movie.getMlong());
                    intent.putExtra("sessionList", (Serializable) sessionList);
                }
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_button:
            case R.id.bottom_choose_movie:
                Intent intent2 = new Intent(List_Session.this, MovieActivity.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);

                if (type.equals("管理员")) {
                    intent2.putExtra("cid", cinema.getCid());
                }
                startActivity(intent2);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = null;
                if (type.equals("管理员")) {
                    intent3 = new Intent(List_Session.this, List_Hall.class);
                    intent3.putExtra("cid", cinema.getCid());
                } else {
                    intent3 = new Intent(List_Session.this, CinemaActivity.class);
                }
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = null;
                if (type.equals("用户")) {
                    intent4 = new Intent(List_Session.this, My_User.class);
                } else if (type.equals("管理员")) {
                    intent4 = new Intent(List_Session.this, List_Uh.class);
                    intent4.putExtra("cid", cinema.getCid());
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

    /**
     * @param drawableId  drawableLeft  drawableTop drawableBottom 所用的选择器 通过R.drawable.xx 获得
     * @param radioButton 需要限定图片大小的radioButton
     */
    private void setBounds(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形  (这里的长和宽写死了 自己可以可以修改成 形参传入)
        drawable_news.setBounds(0, 0, 120, 120);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(null, drawable_news, null, null);
    }

    private void loadMovie() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieRepository movieRepository = new MovieRepository();
                movie = movieRepository.getMovieByMid(mid);
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void run() {
                        initDate();
                        initSession();
                        if (type.equals("管理员")) {
                            movie_card.setVisibility(View.VISIBLE);
                            cinema_card.setVisibility(View.GONE);
                            byte[] image = Base64.decode(movie.getImgString(), Base64.DEFAULT);
                            Bitmap decodedImg = BitmapFactory.decodeByteArray(
                                    image, 0, image.length);
                            ImageView movie_img = (ImageView)
                                    findViewById(R.id.list_session_movie_img);
                            movie_img.setImageBitmap(decodedImg);

                            //按照四舍五入的方法保留一位小数
                            DecimalFormat f = new DecimalFormat("#0.0");
                            TextView movie_sc = (TextView) findViewById(R.id.list_session_movie_sc);
                            movie_sc.setText(f.format(
                                    movie.getMscall() * 1.0 / movie.getMscnum()) + "分");

                            TextView movie_scnum = (TextView) findViewById(
                                    R.id.list_session_movie_scnum);
                            movie_scnum.setText(movie.getMscnum() + "人参评");

                            TextView movie_name = (TextView) findViewById(R.id.list_session_movie_name);
                            movie_name.setText(movie.getMname());

                            TextView movie_type = (TextView) findViewById(R.id.list_session_movie_type);
                            TextView movie_long = (TextView) findViewById(R.id.list_session_movie_long);
                            TextView movie_pf = (TextView) findViewById(R.id.list_session_movie_pf);
                            movie_long.setText(movie.getMlong() + "分钟");
                            movie_type.setText(movie.getMtype());
                            movie_pf.setText(movie.getMpf() + "元");

                            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            String showdate = sdf.format(movie.getShowdate());
                            String downdate = sdf.format(movie.getDowndate());
                            TextView movie_date = (TextView) findViewById(R.id.list_session_movie_date);
                            movie_date.setText(showdate + "--" + downdate);

                            TextView movie_story = (TextView) findViewById(
                                    R.id.list_session_movie_story);
                            movie_story.setText(movie.getMstory());

                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
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

    //计算日期差
    public int getDateDiff(Date startDate, Date endDate) {
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return days;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)

    private void initDate() {

        Date currentDate = type.equals("用户") ? getNowDate() : movie.getShowdate();

        Date downtime = movie.getDowndate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int daynums = getDateDiff(currentDate, downtime);

        for (int i = 0; i <= daynums; i++) {
            Date datei = new Date(currentDate.getTime() + i * 24 * 3600 * 1000L);
            String re = dateFormat.format(datei);
            dateList.add(re.substring(5));
            realDateList.add(datei);
        }

    }

    private void initSession() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                SessionRepository sessionRepository = new SessionRepository();
                sessionList = sessionRepository.findAllSession(cid, movie.getMid());
                if (sessionList.size() == 0) {
                    msg = 1;
                }
                HallRepository hallRepository = new HallRepository();
                hallList = hallRepository.findAllHall(cid);

                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    private void initSession(Date date) {

        chooseSessionList.clear();
        double pf = 0;
        for (Session session : sessionList) {
            if (session.getShowDate().getTime() == date.getTime()) {
                chooseSessionList.add(session);
                String state = session.getState();
                int count = 0;
                for (int j = 0; j < state.length(); j++) {
                    if (state.charAt(j) == '1') {
                        count++;
                    }
                }
                pf += count * session.getPrice();
            }
        }

        if (chooseSessionList.size() == 0) {
            Toast.makeText(List_Session.this, "当前日期没有场次", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new MyAdapter();
            sessionView.setAdapter(adapter);
        }

        if (type.equals("管理员")) {
            pftext.setVisibility(View.VISIBLE);
            pftext.setText("本日票房:" + String.format("%.2f", pf));
        }
        progressBar.setVisibility(View.GONE);

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chooseSessionList.size();
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
                view1 = inflater.inflate(R.layout.item_session, null);
            } else view1 = view;

            TextView showTime = (TextView) view1.findViewById(R.id.item_session_showTime);
            TextView hname = (TextView) view1.findViewById(R.id.item_session_hname);
            TextView price = (TextView) view1.findViewById(R.id.item_session_price);
            ProgressBar progressBar = (ProgressBar)
                    view1.findViewById(R.id.item_session_send_progress);

            Session session = chooseSessionList.get(i);

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            showTime.setText(dateFormat.format(session.getShowTime()) + "——" +
                    dateFormat.format(session.getEndTime()));

            int hall_row = 0, hall_col = 0;

            for (Hall hall : hallList) {
                if (hall.getHid() == session.getHid()) {
                    hname.setText(hall.getHname());
                    hall_row = hall.getRow();
                    hall_col = hall.getColumn();
                    break;
                }
            }

            price.setText("￥" + session.getPrice());

            if (type.equals("管理员") && getDateDiff(session.getShowDate(), getNowDate()) >= 0) {
                progressBar.setVisibility(View.VISIBLE);
                String[] state = session.getState().split(",");
                List<String> seatList = new ArrayList<>();
                int count = 0;
                for (int j = 0; j < state.length; j++) {
                    for (int k = 0; k < state[j].length(); k++) {
                        if (state[j].charAt(k) == '1') {
                            count++;
                        }
                        seatList.add(state[j].charAt(k) + "");
                    }
                }
                int progress = (int) ((count * 1.0 / (hall_row * hall_col)) * 100);
                progressBar.setProgress(progress);
                progressBar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RecyclerView recyclerView = new RecyclerView(view1.getContext());
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                                view1.getContext(), state[0].length());
                        recyclerView.setLayoutManager(gridLayoutManager);
                        SeatAdapter seatAdapter = new SeatAdapter(seatList);
                        recyclerView.setAdapter(seatAdapter);

                        LinearLayout ll = new LinearLayout(view1.getContext());
                        ll.setOrientation(LinearLayout.VERTICAL);
                        ll.addView(recyclerView);
                        ll.setEnabled(false);

                        AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());
                        builder.setView(ll);
                        builder.setTitle("座位情况");
                        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
            }

            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                initSession(realDateList.get(0));
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(),
                        "暂无安排场次", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    };
}