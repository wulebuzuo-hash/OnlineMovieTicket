package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Comment;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.fragment.Census_Ticket;
import com.android.onlinemovieticket.fragment.Lay_bottom;
import com.android.onlinemovieticket.fragment.Soon_ticket;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.CommentRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;
import com.android.onlinemovieticket.service.TicketNotificate_IntentService;

import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class List_Ticket extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private RadioButton btn_census;

    private List<Movie> movieList = new ArrayList<>();

    private List<Ticket> ticketList;
    private List<Session> sessionList;
    private String account;
    private String type;

    private Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ticket);

        ticketList = (List<Ticket>) getIntent().getSerializableExtra("ticketList");
        sessionList = (List<Session>) getIntent().getSerializableExtra("sessionList");
        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("电影票");

        btn_census = (RadioButton) findViewById(R.id.list_ticket_btn_census);
        btn_census.setOnClickListener(this);
        Drawable drawable_news = getResources().getDrawable(R.drawable.pc_census);
        drawable_news.setBounds(0, 0, 100, 100);
        btn_census.setCompoundDrawables(drawable_news, null, null, null);

        initTicket();

        showFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.list_ticket_btn_census:
                outDrawer_Census();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> toValue() {
        List<String> list = new ArrayList<>();

        int all_num = ticketList.size();
        list.add(0, all_num + "部");
        double all_sum = 0;
        for (int i = 0; i < ticketList.size(); i++) {
            all_sum += ticketList.get(i).getPrice();
        }
        list.add(1, "￥ " + String.format("%.2f", all_sum));

        String movie_name = "";

        List<String> list_type = new LinkedList<>();
        for (int i = 0; i < movieList.size(); i++) {
            Movie mm = movieList.get(i);
            String[] type = mm.getMtype().split("/");
            for (String str : type) {
                list_type.add(str);
            }

            if (comment.getMid() == mm.getMid()) {
                movie_name = mm.getMname();
            }
        }

        list_type.sort(Comparator.naturalOrder());

        String type_1 = list_type.get(0);
        int num_1 = Collections.frequency(list_type, type_1);
        int progress_1 = (int) ((double) num_1 / all_num) * 100;
        list.add(2, type_1);
        list.add(3, progress_1 + "");

        if(num_1 == list_type.size()){
            list.add(4, "暂无");
            list.add(5, "0");
            list.add(6, "暂无");
            list.add(7, "0");
        }else {
            String type_2 = list_type.get(num_1);
            int num_2 = Collections.frequency(list_type, type_2);
            int progress_2 = (int) ((double) num_2 / all_num) * 100;
            list.add(4, type_2);
            list.add(5, progress_2 + "");

            if(num_1 + num_2 == list_type.size()){
                list.add(6, "暂无");
                list.add(7, "0");
            }else {
                String type_3 = list_type.get(num_1 + num_2);
                int num_3 = Collections.frequency(list_type, type_3);
                int progress_3 = (int) ((double) num_3 / all_num) * 100;
                list.add(6, type_3);
                list.add(7, progress_3 + "");
            }
        }

        list.add(8,movie_name);
        list.add(9, comment.getSc() + " 分");
        list.add(10, comment.getComment_text());
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void outDrawer_Census(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) toValue());
        Fragment census = new Census_Ticket();
        census.setArguments(bundle);
        ft.add(R.id.list_ticket_drawer_frame, census).commit();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.list_ticket_drawer);
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void showFragment() {
        FragmentManager manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);

        Fragment bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);

        Fragment list_fragment = new Soon_ticket();
        list_fragment.setArguments(bundle);
        transaction.add(R.id.list_ticket_frame, bottom_fragment).
                add(R.id.list_ticket_list_frame, list_fragment).commit();
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    /**
     * 获取时间差
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
     * 从数据库查询所有当前账号的电影票
     */
    private void initTicket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieRepository movieRepository = new MovieRepository();
                CommentRepository commentRepository = new CommentRepository();
                comment = commentRepository.getCommentByAccount(account);

                int msg = 0;

                for (Session session : sessionList) {
                    movieList.add(movieRepository.getMovieByMid(session.getMid()));
                }

                if (movieList.size() == 0) {
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
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "查询电影票失败", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                Toast.makeText(List_Ticket.this,
                        "删除成功", Toast.LENGTH_LONG).show();
                initTicket();
            } else if (msg.what == 5) {
                Toast.makeText(List_Ticket.this,
                        "删除失败", Toast.LENGTH_LONG).show();
            }

        }
    };
}