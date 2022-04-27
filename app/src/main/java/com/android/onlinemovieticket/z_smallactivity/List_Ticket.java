package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;
import com.android.onlinemovieticket.service.TicketNotificate_IntentService;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class List_Ticket extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private EditText searchEdit;
    private Button searchButton;
    private ProgressBar progressBar;

    private List<Movie> movieList = new ArrayList<>();
    private List<Hall> hallList = new ArrayList<>();
    private List<Cinema> cinemaList = new ArrayList<>();

    private List<Ticket> showTicketList = new ArrayList<>();
    private List<Session> showSessionList = new ArrayList<>();
    private List<Movie> showMovieList = new ArrayList<>();
    private List<Hall> showHallList = new ArrayList<>();
    private List<Cinema> showCinemaList = new ArrayList<>();

    private ListView ticketView;
    private MyAdapter adapter;

    private List<Ticket> ticketList;
    private List<Session> sessionList;
    private String account;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_ticket);

        ticketList = (List<Ticket>) getIntent().getSerializableExtra("ticketList");
        showTicketList = ticketList;
        sessionList = (List<Session>) getIntent().getSerializableExtra("sessionList");
        showSessionList = sessionList;

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("电影票");

        searchEdit = (EditText) findViewById(R.id.list_ticket_searchEdit);
        searchButton = (Button) findViewById(R.id.list_ticket_searshButton);
        searchButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.list_ticket_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        ticketView = (ListView) findViewById(R.id.list_ticket_ticketView);
        initTicket();

        ticketView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(List_Ticket.this, List_Comment.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie", showMovieList.get(position));
                intent.putExtras(bundle);

                Session session = showSessionList.get(position);
                Ticket ticket = showTicketList.get(position);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                ParsePosition pos = new ParsePosition(0);
                Date endTime = sdf3.parse(sdf2.format(session.getShowDate()) + " "
                        + sdf1.format(session.getEndTime()), pos);
                Date nowTime = getNowDate();
                if (getTime(endTime, nowTime) > 0 && ticket.getIsGrade() == 0) {
                    intent.putExtra("isbuy", true);
                    updateIsGrade(ticket.getTid(), 1);

                    Intent stopIntent = new Intent(List_Ticket.this,
                            TicketNotificate_IntentService.class);
                    stopService(stopIntent);
                    Intent startIntent = new Intent(List_Ticket.this,
                            TicketNotificate_IntentService.class);
                    startIntent.putExtra("account", account);
                    startIntent.putExtra("type", type);
                    startService(startIntent);

                } else {
                    intent.putExtra("isbuy", false);
                }

                startActivity(intent);
            }
        });

        ticketView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Ticket ticket = showTicketList.get(position);
                Movie movie = showMovieList.get(position);
                Session session = showSessionList.get(position);
                Hall hall = showHallList.get(position);
                Cinema cinema = showCinemaList.get(position);
                updateTickets(ticket, movie, hall, cinema, session);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.list_ticket_searshButton:
                String search = searchEdit.getText().toString();
                if (search.equals("")) {
                    Toast.makeText(List_Ticket.this,
                            "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchTicket(search);
                }
                break;
            case R.id.bottom_choose_movie:
                Intent intent2 = new Intent(List_Ticket.this, MovieActivity.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);
                startActivity(intent2);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = new Intent(List_Ticket.this, CinemaActivity.class);
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = new Intent(List_Ticket.this, My_User.class);
                intent4.putExtra("account", account);
                intent4.putExtra("type", type);
                startActivity(intent4);
                finish();
                break;
            default:
                break;
        }
    }

    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    private void updateIsGrade(int tid, int isGrade) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                TicketRepository ticketRepository = new TicketRepository();
                boolean flag = ticketRepository.updateIsGrade(tid, isGrade);
            }
        }).start();
    }

    private void updateTickets(Ticket ticket, Movie movie, Hall hall,
                               Cinema cinema, Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Ticket.this);
        builder.setTitle("提示");
        String[] choice = {"退票", "改签"};
        List<Integer> choiceList = new ArrayList<>();
        choiceList.add(0);
        builder.setSingleChoiceItems(choice, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(List_Ticket.this, "选择了" + choice[which],
                                Toast.LENGTH_SHORT).show();
                        choiceList.clear();
                        choiceList.add(which);
                    }
                });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (choiceList.get(0)) {
                    case 0:
                        delConfirm(ticket, session);
                        break;
                    case 1:
                        changefirm(ticket, movie, hall, cinema, session);
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
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

    private void delConfirm(Ticket ticket, Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Ticket.this);
        builder.setTitle("提示");
        builder.setMessage("是否删除该电影票？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTicket(ticket, session, null, null, null, -1);
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

    private void deleteTicket(Ticket ticket, Session session, Movie movie,
                              Hall hall, Cinema cinema, int index) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 5;
                TicketRepository ticketRepository = new TicketRepository();
                boolean flag1 = ticketRepository.deleteTicket(ticket);
                if (flag1) {
                    String[] ticket_seat = ticket.getSeat().split(";");
                    int[] seat_row = new int[ticket_seat.length];
                    int[] seat_column = new int[ticket_seat.length];
                    for (int i = 0; i < ticket_seat.length; i++) {
                        String[] seat = ticket_seat[i].split(",");
                        seat_row[i] = Integer.parseInt(seat[0]) - 1;
                        seat_column[i] = Integer.parseInt(seat[1]) - 1;
                    }

                    String re = session.getState();
                    String[] state = re.split(",");
                    for (int index_row = 0, index_column = 0; index_row < seat_row.length;
                         index_row++, index_column++) {
                        int real_row = seat_row[index_row] * (state[0].length() + 1);
                        int real_index = real_row + seat_column[index_column];
                        re = re.substring(0, real_index) + "0" + re.substring(real_index + 1);

                    }
                    SessionRepository sessionRepository = new SessionRepository();
                    boolean flag2 = sessionRepository.updateState(session.getSid(), re);
                    MovieRepository movieRepository = new MovieRepository();
                    boolean flag3 = movieRepository.updatePf(movieRepository.getMpfByMid(
                            session.getMid())-ticket.getPrice(),session.getMid());
                    if (flag2 && flag3) {
                        if (index == -1) {
                            msg = 4;
                        } else {
                            msg = 6;
                        }
                        session.setState(re);
                        for (int i = 0; i < ticketList.size(); i++) {
                            if (ticketList.get(i).getTid() == ticket.getTid()) {
                                ticketList.remove(i);
                                sessionList.remove(i);
                                hallList.remove(i);
                                cinemaList.remove(i);
                                initTicket();
                                break;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (index) {
                                    case 0:
                                        Intent intent0 = new Intent(List_Ticket.this,
                                                MovieActivity.class);
                                        intent0.putExtra("account", account);
                                        intent0.putExtra("type", type);
                                        intent0.putExtra("ticket_price", ticket.getPrice());
                                        startActivity(intent0);
                                        finish();
                                        break;
                                    case 1:
                                        Intent intent1 = new Intent(List_Ticket.this,
                                                CinemaActivity.class);
                                        intent1.putExtra("account", account);
                                        intent1.putExtra("type", type);
                                        intent1.putExtra("ticket_price", ticket.getPrice());

                                        Bundle bundle1 = new Bundle();
                                        bundle1.putSerializable("movie", movie);
                                        intent1.putExtras(bundle1);
                                        startActivity(intent1);
                                        finish();
                                        break;
                                    case 2:
                                        Intent intent2 = new Intent(List_Ticket.this,
                                                List_Session.class);
                                        intent2.putExtra("account", account);
                                        intent2.putExtra("type", type);
                                        intent2.putExtra("cid", cinema.getCid());
                                        intent2.putExtra("ticket_price", ticket.getPrice());

                                        Bundle bundle2 = new Bundle();
                                        bundle2.putSerializable("movie", movie);
                                        intent2.putExtras(bundle2);
                                        startActivity(intent2);
                                        finish();
                                        break;
                                    case 3:
                                        Intent intent3 = new Intent(List_Ticket.this,
                                                Choose_Seat.class);
                                        intent3.putExtra("account", account);
                                        intent3.putExtra("type", type);
                                        intent3.putExtra("ticket_price", ticket.getPrice());
                                        intent3.putExtra("movieName", movie.getMname());
                                        intent3.putExtra("hname", hall.getHname());

                                        Bundle bundle3 = new Bundle();
                                        bundle3.putSerializable("session", session);
                                        intent3.putExtras(bundle3);
                                        startActivity(intent3);
                                        finish();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    private void changefirm(Ticket ticket, Movie movie, Hall hall, Cinema cinema, Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Ticket.this);
        builder.setTitle("提示");
        String[] choice = new String[]{"更换电影", "更换影院", "更换场次", "更换座位"};
        List<Integer> choiceList = new ArrayList<>();
        choiceList.add(0);
        builder.setSingleChoiceItems(choice,
                0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(List_Ticket.this, "选择了" + choice[which],
                                Toast.LENGTH_SHORT).show();
                        choiceList.clear();
                        choiceList.add(which);
                    }
                });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteTicket(ticket, session, movie, hall, cinema, choiceList.get(0));
                dialog.dismiss();
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


    private void initTicket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MovieRepository movieRepository = new MovieRepository();
                CinemaRepository cinemaRepository = new CinemaRepository();
                HallRepository hallRepository = new HallRepository();

                int msg = 0;

                for (Session session : sessionList) {
                    movieList.add(movieRepository.getMovieByMid(session.getMid()));
                    showMovieList.add(movieRepository.getMovieByMid(session.getMid()));

                    hallList.add(hallRepository.getHallByHid(session.getHid()));
                    showHallList.add(hallRepository.getHallByHid(session.getHid()));

                    cinemaList.add(cinemaRepository.getCinemaByCid(session.getCid()));
                    showCinemaList.add(cinemaRepository.getCinemaByCid(session.getCid()));
                }

                if (movieList.size() == 0) {
                    msg = 1;
                }
            }
        }).start();
    }

    private void searchTicket(String search) {
        showTicketList.clear();
        showHallList.clear();
        showCinemaList.clear();
        showMovieList.clear();
        showSessionList.clear();

        boolean flag = false;

        for (Movie movie : movieList) {
            if (movie.getMname().contains(search)) {
                flag = true;
                showMovieList.add(movie);
                break;
            }
        }

        if (flag) {
            for (Movie movie : showMovieList) {
                for (Session session : sessionList) {
                    if (movie.getMid() == session.getMid()) {
                        showSessionList.add(session);
                        int i = sessionList.indexOf(session);

                        showHallList.add(hallList.get(i));
                        showCinemaList.add(cinemaList.get(i));
                        showTicketList.add(ticketList.get(i));
                    }
                }
            }

            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } else {
            Toast.makeText(List_Ticket.this,
                    "没有搜索到相关信息", Toast.LENGTH_SHORT).show();
            initTicket();
        }
    }

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
                view1 = inflater.inflate(R.layout.item_ticket, null);
            } else view1 = view;

            ImageView ticket_img = (ImageView) view1.findViewById(R.id.item_ticket_image);
            TextView ticket_price = (TextView) view1.findViewById(R.id.item_ticket_price);
            TextView ticket_name = (TextView) view1.findViewById(R.id.item_ticket_name);
            TextView ticket_code = (TextView) view1.findViewById(R.id.item_ticket_code);
            TextView ticket_seat = (TextView) view1.findViewById(R.id.item_ticket_seat);
            TextView ticket_time = (TextView) view1.findViewById(R.id.item_ticket_time);
            TextView ticket_cinema = (TextView) view1.findViewById(R.id.item_ticket_cinema);
            TextView ticket_hall = (TextView) view1.findViewById(R.id.item_ticket_hall);

            Ticket ticket = showTicketList.get(i);
            Session session = showSessionList.get(i);
            Movie movie = showMovieList.get(i);
            String hname = showHallList.get(i).getHname();
            String cname = showCinemaList.get(i).getCname();

            byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes,
                    0, imageBytes.length);
            ticket_img.setImageBitmap(decodedImage);
            ticket_name.setText(movie.getMname());
            ticket_code.setText(ticket.getTicket_code());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
            String date = dateFormat.format(session.getShowDate());
            String time = dateFormat2.format(session.getShowTime());
            ticket_time.setText(date + " " + time);
            ticket_cinema.setText(cname);
            ticket_hall.setText(hname);
            ticket_price.setText("￥ " + String.valueOf(ticket.getPrice()));

            String seat = ticket.getSeat();
            seat = seat.replace(",", "排");
            seat = seat.replace(";", "座 ");
            ticket_seat.setText(seat);

            return view1;
        }
    }


    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                adapter = new MyAdapter();
                ticketView.setAdapter(adapter);
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
            progressBar.setVisibility(View.GONE);
        }
    };
}