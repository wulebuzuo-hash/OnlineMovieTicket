package com.android.onlinemovieticket;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.adapter.MovieAdapter;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.fragment.Lay_bottom;
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
    private ImageButton addMovie;

    private EditText searchEdit;
    private Button searchButton;
    private ProgressBar progressBar;

    private RecyclerView showingMovie;
    private List<Movie> showingMovieList = new ArrayList<>();
    private MovieAdapter showAdapter;
    private RecyclerView willMovie;
    private List<Movie> willMovieList = new ArrayList<>();
    private MovieAdapter willAdapter;

    private FragmentManager manager;
    private Fragment bottom_fragment;

    private String account;
    private String type;
    private double ticket_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page_1);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("电影");
        addMovie = (ImageButton) findViewById(R.id.title_button_add);
        addMovie.setVisibility(View.VISIBLE);
        addMovie.setOnClickListener(this);

        searchEdit = (EditText) findViewById(R.id.m1_searchEdit);
        searchButton = (Button) findViewById(R.id.m1_searshButton);
        searchButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.m1_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        ticket_price = getIntent().getDoubleExtra("ticket_price", 0.0);

        addBottom();
        if(ticket_price != 0.0) {
            manager.beginTransaction().remove(bottom_fragment).commit();
        }else {
            manager.beginTransaction().show(bottom_fragment).commit();
        }

        if (type.equals("BOSS")) {
            addMovie.setVisibility(View.VISIBLE);
        } else {
            addMovie.setVisibility(View.GONE);
        }

        showingMovie = (RecyclerView) findViewById(R.id.m1_showingRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        showingMovie.setLayoutManager(layoutManager);
        showAdapter = new MovieAdapter(showingMovieList, account, type, ticket_price);
        showingMovie.setAdapter(showAdapter);

        willMovie = (RecyclerView) findViewById(R.id.m1_willRecyclerView);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        willMovie.setLayoutManager(layoutManager2);
        willAdapter = new MovieAdapter(willMovieList, account, type, ticket_price);
        willMovie.setAdapter(willAdapter);
        initMovies();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                loginConfirm();
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
            case R.id.title_button_add:
                addConfirm();
                break;
            default:
                break;
        }

    }

    private void addBottom(){
        manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);
        bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.m1_frame, bottom_fragment).commit();
    }

    /**
     * 从数据库加载所有正在上映的和即将上映的电影
     */
    private void initMovies() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                MovieRepository movieRepository = new MovieRepository();
                List<Movie> allMovieList = movieRepository.findAllMovie();

                if (allMovieList.size() == 0) {
                    msg = 1;
                } else {
                    Date currentDate = getNowDate();
                    for(Movie movie : allMovieList){
                        if(movie.getDowndate().compareTo(currentDate) > 0){
                            if(movie.getShowdate().compareTo(currentDate) <= 0){
                                showingMovieList.add(movie);
                            }else {
                                willMovieList.add(movie);
                            }
                        }
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    /**
     * 获取当前日期
     * @return
     */
    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    /**
     * 根据电影名提示，搜索电影
     * @param movieName
     */
    private void searchMovie(String movieName) {
        List<Movie> showtemplist = new ArrayList<>();
        for(Movie movie:showingMovieList){
            if(movie.getMname().contains(movieName)){
                showtemplist.add(movie);
            }
        }
        List<Movie> willtemplist = new ArrayList<>();
        for(Movie movie:willMovieList){
            if(movie.getMname().contains(movieName)){
                willtemplist.add(movie);
            }
        }

        if(showtemplist.size() == 0 && willtemplist.size() == 0){
            Toast.makeText(MovieActivity.this,
                    "没有搜索到相关电影", Toast.LENGTH_SHORT).show();
        }else {
            showingMovieList.clear();
            showingMovieList.addAll(showtemplist);
            showAdapter.notifyDataSetChanged();

            willMovieList.clear();
            willMovieList.addAll(willtemplist);
            willAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 添加电影确认
     */
    private void addConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加电影？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MovieActivity.this, Info_Movie.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
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
     * 返回登录界面确认
     */
    private void loginConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回登录页面？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MovieActivity.this, LoginActivity.class);
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


    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                showAdapter.notifyDataSetChanged();
                willAdapter.notifyDataSetChanged();
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(),
                        "展示电影失败！", Toast.LENGTH_SHORT).show();
            }
        }
    };
}