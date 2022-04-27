package com.android.onlinemovieticket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.service.MyService;
import com.android.onlinemovieticket.z_smallactivity.Info_Movie;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.z_smallactivity.List_Admin;
import com.android.onlinemovieticket.z_smallactivity.List_Hall;
import com.android.onlinemovieticket.z_smallactivity.List_Session;
import com.android.onlinemovieticket.z_smallactivity.List_Uh;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieActivity extends AppCompatActivity implements View.OnClickListener {


    private Button navButton;
    private TextView titleName;

    private EditText searchEdit;
    private Button searchButton;

    private CardView cinema_card;
    private TextView cinema_name;
    private TextView cinema_position;
    private TextView cinema_call;
    private ImageButton cinema_map;
    private List<Session> sessionList = new ArrayList<>();

    private Button addMovie;
    private ProgressBar progressBar;

    private RadioButton showing;
    private boolean showing_flag = false;
    private RadioButton upcoming;

    private List<Movie> allMovieList = new ArrayList<>();
    private List<Movie> showMovieList = new ArrayList<>();
    private ListView movieView;
    private MyAdapter adapter;
//    private RecyclerView movieView;
//    private MovieAdapter adapter;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private String account;
    private String type;
    private Cinema cinema;
    private int cid;
    private double ticket_price;

    private MyService.TicketNotificateBinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MyService.TicketNotificateBinder) iBinder;
            binder.startNotification();
            binder.getProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_1);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("电影");

        searchEdit = (EditText) findViewById(R.id.m1_searchEdit);
        searchButton = (Button) findViewById(R.id.m1_searshButton);
        searchButton.setOnClickListener(this);

        bottom_1 = (RadioButton) findViewById(R.id.bottom_choose_movie);
        setBounds(R.drawable.pc_movie,bottom_1);
        bottom_1.setOnClickListener(this);

        bottom_2 = (RadioButton) findViewById(R.id.bottom_choose_cinema);
        setBounds(R.drawable.pc_cinema,bottom_2);
        bottom_2.setOnClickListener(this);

        bottom_3 = (RadioButton) findViewById(R.id.bottom_choose_my);
        setBounds(R.drawable.my,bottom_3);
        bottom_3.setOnClickListener(this);

        addMovie = (Button) findViewById(R.id.m1_addMovie);
        addMovie.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.m1_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        cinema_card = (CardView) findViewById(R.id.m1_cinema_card);
        cinema_name = (TextView) findViewById(R.id.m1_cinema_name);
        cinema_position = (TextView) findViewById(R.id.m1_cinema_position);
        cinema_call = (TextView) findViewById(R.id.m1_cinema_call);
        cinema_map = (ImageButton) findViewById(R.id.m1_cinema_map);
        cinema_map.setOnClickListener(this);

        showing = (RadioButton) findViewById(R.id.m1_showing);
        showing.setOnClickListener(this);
        upcoming = (RadioButton) findViewById(R.id.m1_upcoming);
        upcoming.setOnClickListener(this);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0.0);

        if (type.equals("BOSS")) {
            addMovie.setVisibility(View.VISIBLE);
            bottom_3.setText("管理员");
        } else if (type.equals("管理员")) {
            addMovie.setVisibility(View.GONE);
            bottom_2.setText("放映厅");
            cid = getIntent().getIntExtra("cid", 0);
        } else {
            addMovie.setVisibility(View.GONE);
        }

        movieView = (ListView) findViewById(R.id.m1_movieView);
        cinema = (Cinema) getIntent().getSerializableExtra("cinema");
        if (cinema != null) {
            cinema_card.setVisibility(View.VISIBLE);
            cinema_name.setText(cinema.getCname());
            cinema_position.setText(cinema.getCposition());
            cinema_call.setText(cinema.getCcall());
            initMovies(cinema.getCid());
        } else {
            cinema_card.setVisibility(View.GONE);
            initMovies(-1);
        }

        movieView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = showMovieList.get(i);
                Intent intent = null;

                if (type.equals("BOSS")) {
                    intent = new Intent(MovieActivity.this, Info_Movie.class);
                } else if (type.equals("管理员")) {
                    intent = new Intent(MovieActivity.this, List_Session.class);
                    intent.putExtra("cid", cid);
                } else if (type.equals("用户")) {
                    if (cinema != null) {
                        intent = new Intent(MovieActivity.this, List_Session.class);
                        intent.putExtra("cid", cinema.getCid());
                        intent.putExtra("ticket_price", ticket_price);
                    } else {
                        intent = new Intent(MovieActivity.this, CinemaActivity.class);
                    }
                }

                intent.putExtra("mid",movie.getMid());
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }

        });

        movieView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = showMovieList.get(i);
                if (type.equals("BOSS")) {
                    delConfirm(movie.getMid(), movie.getMname());
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
                if(cinema !=null){
                    intent = new Intent(MovieActivity.this, CinemaActivity.class);
                    intent.putExtra("account", account);
                    intent.putExtra("type", type);
                }else {
                    intent = new Intent(MovieActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
                break;
            case R.id.m1_searshButton:
                String searsh = searchEdit.getText().toString();
                if (searsh.equals("")) {
                    Toast.makeText(MovieActivity.this, "请输入搜索内容",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchMovie(searsh);
                }
                break;
            case R.id.m1_addMovie:
                Intent intent1 = new Intent(MovieActivity.this, Info_Movie.class);
                intent1.putExtra("account", account);
                intent1.putExtra("type", type);
                startActivity(intent1);
                finish();
                break;
            case R.id.m1_showing:
                progressBar.setVisibility(View.VISIBLE);
                if (cinema != null) {
                    initMovies("showing", cinema.getCid());
                } else {
                    initMovies("showing", -1);
                }
                break;
            case R.id.m1_upcoming:
                progressBar.setVisibility(View.VISIBLE);
                if (cinema != null) {
                    initMovies("upcoming", cinema.getCid());
                } else {
                    initMovies("upcoming", -1);
                }
                break;
            case R.id.bottom_choose_movie:
                Intent intent2 = new Intent(MovieActivity.this, MovieActivity.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);

                if (type.equals("管理员")) {
                    intent2.putExtra("cid", cid);
                }

                startActivity(intent2);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = null;
                if (type.equals("管理员")) {
                    intent3 = new Intent(MovieActivity.this, List_Hall.class);
                    intent3.putExtra("cid", cid);
                } else {
                    intent3 = new Intent(MovieActivity.this, CinemaActivity.class);
                }
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = null;
                if (type.equals("用户")) {
                    intent4 = new Intent(MovieActivity.this, My_User.class);
                } else if (type.equals("管理员")) {
                    intent4 = new Intent(MovieActivity.this, List_Uh.class);
                    intent4.putExtra("cid", cid);
                } else if (type.equals("BOSS")) {
                    intent4 = new Intent(MovieActivity.this, List_Admin.class);
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
     *
     * @param drawableId  drawableLeft  drawableTop drawableBottom 所用的选择器 通过R.drawable.xx 获得
     * @param radioButton  需要限定图片大小的radioButton
     */
    private void setBounds(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形  (这里的长和宽写死了 自己可以可以修改成 形参传入)
        drawable_news.setBounds(0, 0, 120, 120);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(null,drawable_news,null, null);
    }




    private void initMovies(int ccid) {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                MovieRepository movieRepository = new MovieRepository();
                allMovieList = movieRepository.findAllMovie();

                if (allMovieList.size() == 0) {
                    msg = 1;
                } else {
                    if (ccid != -1) {
                        SessionRepository sessionRepository = new SessionRepository();
                        sessionList = sessionRepository.getSessionByCid(ccid);
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    private void initMovies(String type, int cid) {
        showMovieList.clear();
        showing_flag = type.equals("showing") ? true : false;
        CardView m1_cardView = (CardView) findViewById(R.id.m1_cardView);
        if (showing_flag) {
            showing.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            showing.setTextColor(Color.parseColor("#009ACD"));
            upcoming.setBackgroundColor(Color.parseColor("#FFBB86FC"));
            upcoming.setTextColor(Color.parseColor("#666666"));

            m1_cardView.setCardBackgroundColor(Color.parseColor("#FFBB86FC"));
        } else {
            upcoming.setBackgroundColor(Color.parseColor("#009ACD"));
            upcoming.setTextColor(Color.parseColor("#666666"));
            showing.setBackgroundColor(Color.parseColor("#009ACD"));
            showing.setTextColor(Color.parseColor("#FFBB86FC"));

            m1_cardView.setCardBackgroundColor(Color.parseColor("#009ACD"));
        }
        Date currentDate = getNowDate();
        for (Movie movie : allMovieList) {
            if (type.equals("showing")) {
                if (movie.getShowdate().compareTo(currentDate) <= 0 &&
                        movie.getDowndate().compareTo(currentDate) >= 0) {
                    showMovieList.add(movie);
                }
            } else if (type.equals("upcoming")) {
                if (movie.getShowdate().compareTo(currentDate) > 0) {
                    showMovieList.add(movie);
                }
            }
        }

        if (cid != -1) {
            List<Integer> midList = new ArrayList<>();
            for (int i = 0; i < sessionList.size(); i++) {
                Session session = sessionList.get(i);
                if (session.getShowDate().compareTo(currentDate) >= 0 &&
                        !midList.contains(session.getMid())) {
                    midList.add(session.getMid());
                } else {
                    sessionList.remove(i);
                    i--;
                }
            }

            for (int i = 0; i < showMovieList.size(); i++) {
                Movie movie = showMovieList.get(i);
                if (!midList.contains(movie.getMid())) {
                    showMovieList.remove(i);
                    i--;
                }
            }
        }

        if (showMovieList.size() == 0) {
            Toast.makeText(MovieActivity.this,
                    "暂无电影排片", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }

    //获取现在时间
    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    //根据电影名提示，搜索电影
    private void searchMovie(String movieName) {
        List<Movie> tempList = showMovieList;
        showMovieList.clear();
        for (Movie movie : tempList) {
            if (movie.getMname().contains(movieName)) {
                showMovieList.add(movie);
            }
        }
        if (showMovieList.size() == 0) {
            Toast.makeText(MovieActivity.this,
                    "没有搜索到该电影", Toast.LENGTH_SHORT).show();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void delConfirm(int id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除电影" + name);
        builder.setMessage("确定删除吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread() {
                    @Override
                    public void run() {
                        MovieRepository movieRepository = new MovieRepository();
                        boolean flag = movieRepository.deleteMovie(id);
                        if (flag) {
                            hand.sendEmptyMessage(4);
                        } else {
                            hand.sendEmptyMessage(5);
                        }
                    }
                }.start();
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

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showMovieList.size();
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
                view1 = inflater.inflate(R.layout.item_movie, null);
            } else view1 = view;

            ImageView image = (ImageView) view1.findViewById(R.id.item_movie_image);
            TextView name = (TextView) view1.findViewById(R.id.item_movie_name);
            TextView name_english = (TextView) view1.findViewById(R.id.item_movie_name_english);
            TextView type1 = (TextView) view1.findViewById(R.id.item_movie_type1);
            TextView type2 = (TextView) view1.findViewById(R.id.item_movie_type2);
            TextView date = (TextView) view1.findViewById(R.id.item_movie_date);
            TextView actor = (TextView) view1.findViewById(R.id.item_movie_actor);
            TextView director = (TextView) view1.findViewById(R.id.item_movie_director);
            TextView score = (TextView) view1.findViewById(R.id.item_movie_sc);

            Movie movie = showMovieList.get(i);

            byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(
                    imageBytes, 0, imageBytes.length);
            image.setImageBitmap(decodedImage);
            name.setText(movie.getMname());
            name_english.setText(movie.getMname_eng());
            score.setText(String.format("%.1f",(double)movie.getMscall()/movie.getMscnum())+"分");
            type1.setText(movie.getMscreen());
            type2.setText(movie.getMtype());
            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String showdate = sdf.format(movie.getShowdate());

            CardView cardView = (CardView) view1.findViewById(R.id.item_movie_card_view);
            if (showing_flag != true) {
                date.setVisibility(View.VISIBLE);
                score.setVisibility(View.GONE);
                cardView.setCardBackgroundColor(Color.parseColor("#009ACD"));
                date.setText(showdate);
            } else {
                date.setVisibility(View.GONE);
                score.setVisibility(View.VISIBLE);
                cardView.setCardBackgroundColor(Color.parseColor("#FFBB86FC"));
            }
            actor.setText(movie.getMactor());
            director.setText(movie.getMdir());
            return view1;
        }
    }


    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                adapter = new MyAdapter();
                movieView.setAdapter(adapter);
                if (cinema != null){
                    initMovies("showing", cinema.getCid());
                }else {
                    initMovies("showing", -1);
                }
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(),
                        "展示电影失败！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 3) {
                Toast.makeText(MovieActivity.this,
                        "搜索失败，请核对电影名", Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(MovieActivity.this,
                        "删除成功", Toast.LENGTH_LONG).show();
                if (cinema != null){
                    initMovies("showing", cinema.getCid());
                }else {
                    initMovies("showing", -1);
                }

            } else if (msg.what == 5) {
                Toast.makeText(MovieActivity.this,
                        "删除失败", Toast.LENGTH_LONG).show();
            }
        }
    };
}