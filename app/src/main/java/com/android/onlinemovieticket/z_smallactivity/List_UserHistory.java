package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.fragment.Lay_bottom;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class List_UserHistory extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;
    private ImageButton addCinema;
    private EditText searchEdit;
    private Button searchButton;

    private List<Ticket> ticketList = new ArrayList<>();
    private List<Ticket> showTicketList = new ArrayList<>();
    private List<Session> sessionList = new LinkedList<>();
    private List<Session> showSessionList = new LinkedList<>();
    private List<Movie> movieList = new ArrayList<>();
    private List<Movie> showMovieList = new ArrayList<>();
    private List<Hall> hallList = new ArrayList<>();
    private List<Hall> showHallList = new ArrayList<>();

    private MyAdapter adapter;
    private ListView userView;

    private ProgressBar progressBar;

    private FragmentManager manager;
    private Fragment bottom_fragment;

    private String account;
    private String type;
    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_user_history);
        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = (TextView) findViewById(R.id.title_name);
        titlename.setText("电影院");
        addCinema = (ImageButton) findViewById(R.id.title_button_add);
        addCinema.setVisibility(View.GONE);

        searchEdit = (EditText) findViewById(R.id.list_user_history_searchEdit);
        searchButton = (Button) findViewById(R.id.list_user_history_searchButton);
        searchButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.list_user_history_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        userView = (ListView) findViewById(R.id.list_user_history_user_view);

        showBottom();
        loadUserHistory();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                loginConfirm();
                break;
            case R.id.list_user_history_searchButton:
                String searsh = searchEdit.getText().toString();
                if (searsh.equals("")) {
                    Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchUserHistory(searsh);
                }
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
        bundle.putInt("cid", cid);
        bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.list_user_history_frame, bottom_fragment).commit();
    }

    private void initUserHistory_all() {
        showTicketList.clear();
        showSessionList.clear();
        showMovieList.clear();
        showHallList.clear();
        for(int i = 0; i < ticketList.size(); i++) {
            showTicketList.add(ticketList.get(i));
            showSessionList.add(sessionList.get(i));
            showMovieList.add(movieList.get(i));
            showHallList.add(hallList.get(i));
        }

        adapter = new MyAdapter();
        userView.setAdapter(adapter);
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
                Intent intent = new Intent(List_UserHistory.this, LoginActivity.class);
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

    private void searchUserHistory(String keyword) {
        showTicketList.clear();
        showSessionList.clear();
        showMovieList.clear();
        showHallList.clear();
        for(int i = 0; i < ticketList.size(); i++) {
            if(ticketList.get(i).getUaccount().contains(keyword)) {
                showTicketList.add(ticketList.get(i));
                showSessionList.add(sessionList.get(i));
                showMovieList.add(movieList.get(i));
                showHallList.add(hallList.get(i));
            }
        }

        progressBar.setVisibility(View.GONE);
        if(showTicketList.size() == 0) {
            Toast.makeText(this, "没有搜索到相关信息", Toast.LENGTH_SHORT).show();
            initUserHistory_all();
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    private void loadUserHistory(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 0;
                TicketRepository ticketRepository = new TicketRepository();
                SessionRepository sessionRepository = new SessionRepository();
                MovieRepository movieRepository = new MovieRepository();
                HallRepository hallRepository = new HallRepository();
                ticketList = ticketRepository.getAllTicket();

                if(ticketList.size() > 0){
                    msg = 1;
                    for(int i = 0; i < ticketList.size(); i++){
                        Session session = sessionRepository.getSessionBySid(ticketList.get(i).getSid());
                        if(session.getCid() == cid){
                            sessionList.add(session);
                            Movie movie = movieRepository.getMovieByMid(session.getMid());
                            movieList.add(movie);
                            Hall hall = hallRepository.getHallByHid(session.getHid());
                            hallList.add(hall);
                        }else {
                            ticketList.remove(i);
                            i--;
                        }
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    /**
     * 初始化影院列表，listView适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showTicketList.size();
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
                view1 = inflater.inflate(R.layout.item_user_history, null);
            } else view1 = view;

            Ticket ticket = showTicketList.get(i);
            Session session = showSessionList.get(i);
            Movie movie = showMovieList.get(i);
            String hname = showHallList.get(i).getHname();

            ImageView movieImage = view1.findViewById(R.id.item_user_history_image);
            byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes,
                    0, imageBytes.length);
            movieImage.setImageBitmap(decodedImage);
            TextView ticket_price = view1.findViewById(R.id.item_user_history_price);
            ticket_price.setText("￥ " + ticket.getPrice());
            TextView account_text = view1.findViewById(R.id.item_user_history_account);
            account_text.setText(ticket.getUaccount());
            TextView movie_name = view1.findViewById(R.id.item_user_history_movie_name);
            movie_name.setText(movie.getMname());
            TextView ticket_code = view1.findViewById(R.id.item_user_history_ticket_code);
            ticket_code.setText(ticket.getTicket_code());
            TextView session_time = view1.findViewById(R.id.item_user_history_ticket_time);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            session_time.setText(sdf1.format(session.getShowDate()) +
                    " " + sdf2.format(session.getShowTime()));
            TextView hall_name = view1.findViewById(R.id.item_user_history_ticket_hall);
            hall_name.setText(hname);
            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(List_UserHistory.this,
                        "获取失败，请检查网络或联系系统管理员", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 1) {
                initUserHistory_all();
            }
        }
    };
}