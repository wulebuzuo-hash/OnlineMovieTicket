package com.android.onlinemovieticket.fragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.SeatAdapter;
import com.android.onlinemovieticket.adapter.Soon_ticket_adapter;
import com.android.onlinemovieticket.adapter.onRecyclerItemClickListener;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Comment;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.CommentRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.TicketRepository;
import com.android.onlinemovieticket.service.TicketNotificate_IntentService;
import com.android.onlinemovieticket.z_smallactivity.Choose_Seat;
import com.android.onlinemovieticket.z_smallactivity.List_Comment;
import com.android.onlinemovieticket.z_smallactivity.List_Session;
import com.android.onlinemovieticket.z_smallactivity.List_Ticket;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Soon_ticket extends Fragment {

    private RecyclerView ticketView;
    private Soon_ticket_adapter adapter;
    private ProgressBar progressBar;


    private List<Ticket> ticketList = new ArrayList<>();
    private List<Ticket> showTicketList = new ArrayList<>();
    private List<Session> sessionList = new LinkedList<>();
    private List<Session> showSessionList = new LinkedList<>();
    private List<Movie> movieList = new ArrayList<>();
    private List<Movie> showMovieList = new ArrayList<>();
    private List<Cinema> cinemaList = new ArrayList<>();
    private List<Cinema> showCinemaList = new ArrayList<>();
    private List<Hall> hallList = new ArrayList<>();
    private List<Hall> showHallList = new ArrayList<>();

    private List<Comment> commentList = new ArrayList<>();

    private EditText searchEdit;
    private Button searchBtn;

    private RadioGroup radioGroup;
    private RadioButton btn_soon;
    private RadioButton btn_saw;

    private String account;
    private String type;

    private int index = -1;
    private double price;
    private int mid;
    private String mname;
    private int cid;
    private String hname;
    private Session ss;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        account = getArguments().getString("account");
        type = getArguments().getString("type");
        View view = inflater.inflate(R.layout.soon_ticket, container, false);
        ticketView = view.findViewById(R.id.soon_ticket_ticketView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        ticketView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) view.findViewById(R.id.soon_ticket_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        loadTicket();

        searchEdit = view.findViewById(R.id.soon_ticket_searchEdit);
        searchBtn = view.findViewById(R.id.soon_ticket_searchBtn);

        radioGroup = view.findViewById(R.id.soon_ticket_radiogroup);
        radioGroup.check(R.id.soon_ticket_btn_soon);
        btn_soon = view.findViewById(R.id.soon_ticket_btn_soon);
        setBounds(R.drawable.pc_will_ticket, btn_soon);
        btn_saw = view.findViewById(R.id.soon_ticket_btn_saw);
        setBounds(R.drawable.pc_saw_ticket, btn_saw);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = searchEdit.getText().toString();
                if (search.equals("")) {
                    Toast.makeText(getContext(),
                            "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchTicket(search);
                }
            }
        });
        btn_soon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                initTicket_soon();
            }
        });
        btn_saw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                initTicket_saw();
            }
        });
    }

    /**
     * @param drawableId  drawableLeft  drawableTop drawableBottom 所用的选择器 通过R.drawable.xx 获得
     * @param radioButton 需要限定图片大小的radioButton
     */
    private void setBounds(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形  (这里的长和宽写死了 自己可以可以修改成 形参传入)
        drawable_news.setBounds(0, 0, 100, 100);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(drawable_news, null, null, null);
    }

    /**
     * 搜索电影票
     *
     * @param search
     */
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
        } else {
            Toast.makeText(getContext(),
                    "没有搜索到相关信息", Toast.LENGTH_SHORT).show();
            initTicket_soon();
        }
        progressBar.setVisibility(View.GONE);
    }

    /**
     * 加载全部待看的电影票
     */
    private void initTicket_soon() {
        showTicketList.clear();
        showHallList.clear();
        showCinemaList.clear();
        showMovieList.clear();
        showSessionList.clear();

        for (int i = 0; i < ticketList.size(); i++) {
            Session session = sessionList.get(i);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = sdf.format(session.getShowDate());
            String timeString = sdf2.format(session.getEndTime());
            ParsePosition pos = new ParsePosition(0);
            Date endTime = sdf3.parse(dateString + " " + timeString, pos);
            Date currentTime = getNowDate();
            if (endTime.after(currentTime)) {
                showTicketList.add(ticketList.get(i));
                showSessionList.add(session);
                showHallList.add(hallList.get(i));
                showCinemaList.add(cinemaList.get(i));
                showMovieList.add(movieList.get(i));
            }
        }
        adapter = new Soon_ticket_adapter(showTicketList, showSessionList,
                showMovieList, showCinemaList, showHallList, true);

        adapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isClick) {
            }

            @Override
            public void onItemClick(int position) {

                String[] state = sessionList.get(position).getState().split(",");
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

                RecyclerView recyclerView = new RecyclerView(getContext());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(
                        getContext(), state[0].length());
                recyclerView.setLayoutManager(gridLayoutManager);
                SeatAdapter seatAdapter = new SeatAdapter(seatList);
                recyclerView.setAdapter(seatAdapter);

                LinearLayout ll = new LinearLayout(getContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(recyclerView);
                ll.setEnabled(false);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

            @Override
            public void onLongClick(int position) {
                Ticket ticket = showTicketList.get(position);
                Movie movie = showMovieList.get(position);
                Session session = showSessionList.get(position);
                Hall hall = showHallList.get(position);
                Cinema cinema = showCinemaList.get(position);
                updateTickets(ticket, movie, hall, cinema, session);
            }
        });

        ticketView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void initTicket_saw() {
        showTicketList.clear();
        showHallList.clear();
        showCinemaList.clear();
        showMovieList.clear();
        showSessionList.clear();

        for (int i = 0; i < ticketList.size(); i++) {
            Session session = sessionList.get(i);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = sdf.format(session.getShowDate());
            String timeString = sdf2.format(session.getEndTime());
            ParsePosition pos = new ParsePosition(0);
            Date endTime = sdf3.parse(dateString + " " + timeString, pos);

            if (endTime.before(new Date())) {
                showTicketList.add(ticketList.get(i));
                showSessionList.add(session);
                showHallList.add(hallList.get(i));
                showCinemaList.add(cinemaList.get(i));
                showMovieList.add(movieList.get(i));
            }
        }
        adapter = new Soon_ticket_adapter(showTicketList, showSessionList,
                showMovieList, showCinemaList, showHallList, false);

        adapter.setListener(new onRecyclerItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isClick) {
            }

            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), List_Comment.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("mid", showMovieList.get(position).getMid());

                if(ticketList.get(position).getIsGrade() == 1){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            TicketRepository ticketRepository = new TicketRepository();
                            ticketRepository.updateIsGrade(ticketList.get(position).getTid(),0);
                        }
                    }).start();
                }
                intent.putExtra("isbuy", ticketList.get(position).getIsGrade() == 1 ?
                        true : false);
                startActivity(intent);
            }

            @Override
            public void onLongClick(int position) {
                Ticket ticket = showTicketList.get(position);
                delConfirm(ticket);
            }
        });

        ticketView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    private void delTicket(Ticket ticket) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("删除购票记录？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTicket(ticket,null,null,null,null,-1);
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
     * 退票或改签页面
     *
     * @param ticket
     * @param movie
     * @param hall
     * @param cinema
     * @param session
     */
    private void updateTickets(Ticket ticket, Movie movie, Hall hall,
                               Cinema cinema, Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        String[] choice = {"退票", "改签"};
        List<Integer> choiceList = new ArrayList<>();
        choiceList.add(0);
        builder.setSingleChoiceItems(choice, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "选择了" + choice[which],
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
     * 退票确认
     *
     * @param ticket
     * @param session
     */
    private void delConfirm(Ticket ticket, Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("是否确认退票？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index = -1;
                deleteTicket(ticket, session, null, null, null, index);
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
     * 删票确认
     * @param ticket
     */
    private void delConfirm(Ticket ticket) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("是否确认删除？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTicket(ticket);
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
     * 执行退票操作
     *
     * @param ticket
     * @param session
     * @param movie
     * @param hall
     * @param cinema
     * @param index
     */
    private void deleteTicket(Ticket ticket, Session session, Movie movie,
                              Hall hall, Cinema cinema, int index) {

        for (int i = 0; i < ticketList.size(); i++) {
            if (ticketList.get(i).getTid() == ticket.getTid()) {
                ticketList.remove(i);
                sessionList.remove(i);
                hallList.remove(i);
                cinemaList.remove(i);
                break;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 5;
                TicketRepository ticketRepository = new TicketRepository();
                boolean flag1 = ticketRepository.deleteTicket(ticket);
                if (flag1) {
                    msg = 4;
                    if(movie != null){
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
                                session.getMid()) - ticket.getPrice(), session.getMid());
                        if (flag2 && flag3) {
                            if (index == -1) {
                                msg = 4;
                            } else {
                                msg = 6;
                                price = ticket.getPrice();
                                mid = movie.getMid();
                                mname = movie.getMname();
                                cid = cinema.getCid();
                                hname = hall.getHname();
                                ss = session;
                            }
                            session.setState(re);
                        }
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    /**
     * 执行删票操作
     * @param ticket
     */
    private void deleteTicket(final Ticket ticket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 8;
                TicketRepository ticketRepository = new TicketRepository();
                boolean flag1 = ticketRepository.deleteTicket(ticket);
                if (flag1) {
                    msg = 7;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
    }

    /**
     * 改签确认
     *
     * @param ticket
     * @param movie
     * @param hall
     * @param cinema
     * @param session
     */
    private void changefirm(Ticket ticket, Movie movie, Hall hall, Cinema cinema, Session session) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        String[] choice = new String[]{"更换电影", "更换影院", "更换场次", "更换座位"};
        List<Integer> choiceList = new ArrayList<>();
        choiceList.add(0);
        builder.setSingleChoiceItems(choice,
                0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "选择了" + choice[which],
                                Toast.LENGTH_SHORT).show();
                        choiceList.clear();
                        choiceList.add(which);
                    }
                });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index = choiceList.get(0);
                deleteTicket(ticket, session, movie, hall, cinema, index);
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

    /**
     * 从数据库获取全部待看的电影票
     */
    private void loadTicket() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                TicketRepository ticketRepository = new TicketRepository();
                ticketList = ticketRepository.getTicketByAccount(account);
                CommentRepository commentRepository = new CommentRepository();
                commentList = commentRepository.getAllCommentByAccount(account);
                if (ticketList.size() == 0) {
                    msg = 1;
                } else {
                    SessionRepository sessionRepository = new SessionRepository();
                    MovieRepository movieRepository = new MovieRepository();
                    CinemaRepository cinemaRepository = new CinemaRepository();
                    HallRepository hallRepository = new HallRepository();

                    for (int i = 0; i < ticketList.size(); i++) {
                        Ticket ticket = ticketList.get(i);
                        Session session = sessionRepository.getSessionBySid(ticket.getSid());
                        sessionList.add(session);
                        movieList.add(movieRepository.getMovieByMid(session.getMid()));
                        hallList.add(hallRepository.getHallByHid(session.getHid()));
                        cinemaList.add(cinemaRepository.getCinemaByCid(session.getCid()));

                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                initTicket_soon();
            } else if (msg.what == 1) {
                Toast.makeText(getContext(), "展示影票失败，请下拉刷新或检查网络", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 4) {
                Toast.makeText(getContext(),
                        "退票成功", Toast.LENGTH_LONG).show();
                initTicket_soon();
            } else if (msg.what == 5) {
                Toast.makeText(getContext(),
                        "退票失败", Toast.LENGTH_LONG).show();
            } else if (msg.what == 6) {
                switch (index) {
                    case 0:
                        Intent intent0 = new Intent(getActivity(),
                                MovieActivity.class);
                        intent0.putExtra("account", account);
                        intent0.putExtra("type", type);
                        intent0.putExtra("ticket_price", price);
                        startActivity(intent0);
                        getActivity().finish();
                        break;
                    case 1:
                        Intent intent1 = new Intent(getActivity(),
                                CinemaActivity.class);
                        intent1.putExtra("account", account);
                        intent1.putExtra("type", type);
                        intent1.putExtra("ticket_price", price);
                        intent1.putExtra("mid", mid);
                        startActivity(intent1);
                        getActivity().finish();
                        break;
                    case 2:
                        Intent intent2 = new Intent(getActivity(),
                                List_Session.class);
                        intent2.putExtra("account", account);
                        intent2.putExtra("type", type);
                        intent2.putExtra("cid", cid);
                        intent2.putExtra("ticket_price", price);
                        intent2.putExtra("mid", mid);
                        startActivity(intent2);
                        getActivity().finish();
                        break;
                    case 3:
                        Intent intent3 = new Intent(getActivity(),
                                Choose_Seat.class);
                        intent3.putExtra("account", account);
                        intent3.putExtra("type", type);
                        intent3.putExtra("ticket_price", price);
                        intent3.putExtra("movieName", mname);
                        intent3.putExtra("hname", hname);

                        Bundle bundle3 = new Bundle();
                        bundle3.putSerializable("session", ss);
                        intent3.putExtras(bundle3);
                        startActivity(intent3);
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }else if(msg.what == 7){
                Toast.makeText(getContext(),
                        "删除成功", Toast.LENGTH_LONG).show();
                initTicket_saw();
            }else if(msg.what == 8){
                Toast.makeText(getContext(),
                        "删除失败", Toast.LENGTH_LONG).show();
            }

        }
    };
}