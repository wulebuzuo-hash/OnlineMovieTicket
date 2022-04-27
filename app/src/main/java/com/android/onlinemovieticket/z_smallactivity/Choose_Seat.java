package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.ChooseSeatAdapter;
import com.android.onlinemovieticket.adapter.SeatAdapter;
import com.android.onlinemovieticket.adapter.onRecyclerItemClickListener;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;
import com.android.onlinemovieticket.repository.UserRepository;
import com.android.onlinemovieticket.service.TicketNotificate_IntentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Choose_Seat extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private TextView movieName;
    private TextView movieTime;
    private TextView movieHall;

    private List<String> seatList = new ArrayList<>();

    private RecyclerView seatView;
    private SeatAdapter seatAdapter;

    private List<Ticket> ticketList = new ArrayList<>();
    private RecyclerView chooseSeatView;
    private ChooseSeatAdapter chooseSeatAdapter;

    private ProgressBar progressBar;
    private Button submit;

    private String account;
    private String type;
    private Session session;
    private String[] state;
    private double ticket_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_seat);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("选座");

        submit = (Button) findViewById(R.id.choose_seat_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.choose_seat_progressbar);
        progressBar.setVisibility(View.GONE);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        session = (Session) getIntent().getSerializableExtra("session");
        state = session.getState().split(",");
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0.0);
        if (ticket_price == 0.0) {
            submit.setText("支付：￥0.00");
        } else {
            submit.setText("剩余：￥" + ticket_price);
        }

        movieName = (TextView) findViewById(R.id.choose_seat_movie_name);
        movieName.setText(getIntent().getStringExtra("movieName"));

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        movieTime = (TextView) findViewById(R.id.choose_seat_movie_time);
        movieTime.setText(sdf.format(session.getShowDate()) + " " +
                sdf2.format(session.getShowTime()));
        movieHall = (TextView) findViewById(R.id.choose_seat_movie_hall);
        movieHall.setText(getIntent().getStringExtra("hname"));


        seatView = (RecyclerView) findViewById(R.id.choose_seat_view);
        initSeat();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, state[0].length());
        seatView.setLayoutManager(gridLayoutManager);
        seatAdapter = new SeatAdapter(seatList);
        seatAdapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isCheck) {
                Ticket ticket = new Ticket(account, session.getSid(),
                        position / state[0].length(),
                        position % state[0].length());
                if (isCheck != true) {
                    addTicket(ticket);
                } else {
                    delTicket(ticket);
                }
            }

            @Override
            public void onLongClick(int position) {}
            @Override
            public void onItemClick(int position) {}
        });
        seatView.setAdapter(seatAdapter);

        chooseSeatView = (RecyclerView) findViewById(R.id.choose_seat_ticket);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        chooseSeatView.setLayoutManager(layoutManager);
        chooseSeatAdapter = new ChooseSeatAdapter(ticketList);
        chooseSeatView.setAdapter(chooseSeatAdapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.choose_seat_submit:
                progressBar.setVisibility(View.VISIBLE);
                submitTicket();
                break;
        }
    }

    private void initSeat() {
        seatList.clear();
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[i].length(); j++) {
                seatList.add(state[i].charAt(j) + "");
            }
        }
    }

    private void addTicket(Ticket ticket) {
        ticketList.add(ticket);
        chooseSeatAdapter.notifyDataSetChanged();

        if (ticket_price != 0.0) {
            ticket_price -= session.getPrice();

            if (ticket_price <= 0) {
                submit.setText("支付：￥" + Math.abs(ticket_price));
            } else {
                submit.setText("剩余：￥" + ticket_price);
            }
        } else {
            ticket_price -= session.getPrice();
            submit.setText("支付：￥" + Math.abs(ticket_price));
        }


    }

    private void delTicket(Ticket ticket) {
        for (int i = 0; i < ticketList.size(); i++) {
            Ticket t = ticketList.get(i);
            if (t.getSeat_Row() == ticket.getSeat_Row() &&
                    t.getSeat_Column() == ticket.getSeat_Column()) {
                ticketList.remove(t);
                i--;
            }
        }
        chooseSeatAdapter.notifyDataSetChanged();

        if (ticket_price != 0.0) {
            ticket_price += session.getPrice();

            if (ticket_price <= 0) {
                submit.setText("支付：￥" + Math.abs(ticket_price));
            } else {
                submit.setText("剩余：￥" + ticket_price);
            }
        } else {
            ticket_price += session.getPrice();
            submit.setText("支付：￥" + Math.abs(ticket_price));
        }
    }

    private void submitTicket() {
        new Thread() {
            @Override
            public void run() {
                SessionRepository sessionRepository = new SessionRepository();
                UserRepository userRepository = new UserRepository();
                int msg = 0;

                char[][] stateChar = new char[state.length][state[0].length()];
                for (int i = 0; i < state.length; i++) {
                    stateChar[i] = state[i].toCharArray();
                }

                String seat = "";
                for (Ticket ticket : ticketList) {
                    seat = seat + (ticket.getSeat_Row() + 1) + "," +
                            (ticket.getSeat_Column() + 1) + ";";
                    stateChar[ticket.getSeat_Row()][ticket.getSeat_Column()] = '1';
                }

                int uid = userRepository.getUserId(account);
                String uidStr = String.valueOf(uid);
                int sid = session.getSid();
                String sidStr = String.valueOf(sid);
                int mid = session.getMid();
                String midStr = String.valueOf(mid);
                String ticket_code = "1" + midStr.substring(midStr.length() - 3) + " "
                        + sidStr.substring(sidStr.length() - 4) + " "
                        + uidStr.substring(uidStr.length() - 4);

                double realTicket_price = getIntent().getDoubleExtra("ticket_price",
                        0.0) - ticket_price;

                if (realTicket_price > 0) {
                    Ticket tt = new Ticket(ticket_code, account, sid, seat,
                            realTicket_price, 0);
                    TicketRepository ticketRepository = new TicketRepository();
                    boolean flag = ticketRepository.addTicket(tt);

                    MovieRepository movieRepository = new MovieRepository();
                    boolean flag2 = movieRepository.updatePf(movieRepository.getMpfByMid(
                            session.getMid())+realTicket_price,session.getMid());
                    if (flag && flag2) {
                        msg = 1;
                    } else {
                        msg = 2;
                    }
                    String reStr = "";
                    for (int i = 0; i < state.length; i++) {
                        for (int j = 0; j < state[i].length(); j++) {
                            reStr += stateChar[i][j];
                        }
                        reStr += ",";
                    }
                    sessionRepository.updateState(session.getSid(), reStr);
                } else {
                    msg = 3;
                }


                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(Choose_Seat.this,
                        "添加失败，连接不上数据库，请检查网络或联系系统管理员！！",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                Toast.makeText(Choose_Seat.this, "添加成功",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

                Intent stopIntent = new Intent(Choose_Seat.this,
                        TicketNotificate_IntentService.class);
                stopService(stopIntent);
                Intent startIntent = new Intent(Choose_Seat.this,
                        TicketNotificate_IntentService.class);
                startIntent.putExtra("account",account);
                startIntent.putExtra("type",type);
                startService(startIntent);

                Intent intent = new Intent(Choose_Seat.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            } else if (msg.what == 2) {
                Toast.makeText(Choose_Seat.this, "添加失败,数据库已连接，但数据不能上传！",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 3) {
                Toast.makeText(Choose_Seat.this, "添加失败，请至少选择一张电影票！",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    };
}