package com.android.onlinemovieticket.z_smallactivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Uh;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.repository.SessionRepository;
import com.android.onlinemovieticket.repository.UhRepository;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Info_Session extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private Button navButton;
    private TextView titleName;

    private TextView movie_name;
    private TextView dateEdit;
    private Date date;
    private Button dateButton;

    private LinearLayout layout;
    private List<LinearLayout> layoutList = new ArrayList<>();
    private List<String> choosetimeList = new ArrayList<>();
    private List<Integer> chooseHidList = new ArrayList<>();
    private List<String> choosestateList = new ArrayList<>();
    private List<EditText> priceEditList = new ArrayList<>();

    private List<String> allTimeList = new ArrayList<>();
    private List<Hall> allhallList = new ArrayList<>();
    private List<Hall> temphallList = new ArrayList<>();
    private List<Integer> allhidList = new ArrayList<>();
    private List<String> allhnameList = new ArrayList<>();

    private ArrayAdapter<String> nameAdapter;
    private ArrayAdapter<String> timeAdapter;
    private Spinner timeSpinner;
    private String showtime;
    private Spinner hallnameSpinner;
    private int chooseHid;
    private EditText priceEdit;
    private String state = "";

    private RadioButton addBtn;
    private RadioButton deleteBtn;

    private Button submit;
    private ProgressBar progressBar;

    private String account;
    private String type;
    private String movieName;
    private int cid;
    private int mid;
    private Movie movie;
    private Session session_update;
    private List<Session> sessionList;

    private int sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_session);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("场次");

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);
        mid = getIntent().getIntExtra("mid", 0);
        movieName = getIntent().getStringExtra("mname");
        session_update = (Session) getIntent().getSerializableExtra("session");
        sessionList = (List<Session>) getIntent().getSerializableExtra("sessionList");
        sid = getIntent().getIntExtra("sid", 0);

        if (mid != 0) {
            loadMovie();
        }

        movie_name = (TextView) findViewById(R.id.info_session_mname);
        movie_name.setText(movieName);
        layout = (LinearLayout) findViewById(R.id.info_session_layout);
        dateEdit = (TextView) findViewById(R.id.info_session_date);
        dateButton = (Button) findViewById(R.id.info_session_dateButton);
        dateButton.setOnClickListener(this);

        priceEdit = (EditText) findViewById(R.id.info_session_price);

        addBtn = (RadioButton) findViewById(R.id.info_session_add);
        addBtn.setOnClickListener(this);
        deleteBtn = (RadioButton) findViewById(R.id.info_session_delete);
        deleteBtn.setOnClickListener(this);

        submit = (Button) findViewById(R.id.info_session_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_session_progressbar);
        progressBar.setVisibility(View.GONE);

        timeSpinner = (Spinner) findViewById(R.id.info_session_time);

        hallnameSpinner = (Spinner) findViewById(R.id.info_session_hall);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.info_session_dateButton:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MARCH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.info_session_add:
                if (layoutList.size() == 0) {
                    choosetimeList.clear();
                    choosetimeList.add(showtime);

                    chooseHidList.clear();
                    chooseHidList.add(chooseHid);

                    choosestateList.clear();
                    choosestateList.add(state);

                    priceEditList.clear();
                    priceEditList.add(priceEdit);
                }
                addSession();
                break;
            case R.id.info_session_delete:
                delSession();
                break;
            case R.id.info_session_submit:
                progressBar.setVisibility(View.VISIBLE);
                if (session_update != null) {
                    submit_updateSession();
                } else {
                    submit_addSession();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String getDate = String.format("%d-%d-%d", i, i1 + 1, i2);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date date = format.parse(getDate, pos);
        dateEdit.setText(format.format(date));

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
                        if (sid != 0) {
                            initSession();
                        } else {
                            initView();
                        }
                    }
                });
            }
        }).start();
    }

    private void initView() {
        initTimes();
        if (session_update != null) {
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
            dateEdit.setText(dateFormat1.format(session_update.getShowDate()));
            allTimeList.add(dateFormat2.format(session_update.getShowTime()));
            addBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        }

        timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, allTimeList);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);
        initHall();
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    showtime = allTimeList.get(position);
                    allTimeList.remove(showtime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                    ParsePosition pos = new ParsePosition(0);
                    if (dateEdit.getText().toString().equals("")) {
                        Toast.makeText(Info_Session.this,
                                "请先选择日期", Toast.LENGTH_SHORT).show();
                    } else {
                        date = format.parse(dateEdit.getText().toString(), pos);
                        initHalls(date, showtime, allhnameList, allhidList);
                        ArrayAdapter<String> hallAdapter = new ArrayAdapter<String>(
                                Info_Session.this, android.R.layout.simple_spinner_item, allhnameList);
                        hallAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        hallnameSpinner.setAdapter(hallAdapter);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        hallnameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    chooseHid = allhidList.get(position - 1);
                    Hall chooseHall = allhallList.get(position - 1);
                    for (int m = 0; m < chooseHall.getRow(); m++) {
                        for (int n = 0; n < chooseHall.getColumn(); n++) {
                            state += "0";
                        }
                        state += ",";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void addSession() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);

        LinearLayout timeLayout = new LinearLayout(this);
        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeLayout.setLayoutParams(params);
        TextView timeText = new TextView(this);
        timeText.setText("放映时间");
        timeText.setTextSize(18);

        List<String> hnameList = new ArrayList<>();
        List<Integer> hidList = new ArrayList<>();
        Spinner hallSpinner = new Spinner(this);

        hallSpinner.setLayoutParams(params);

        Spinner timeSpinner = new Spinner(this);
        timeSpinner.setLayoutParams(params);
        ArrayAdapter<String> timeadapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allTimeList);
        timeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeadapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String showtime = allTimeList.get(i);
                    choosetimeList.add(showtime);
                    allTimeList.remove(showtime);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                    ParsePosition pos = new ParsePosition(0);
                    date = format.parse(dateEdit.getText().toString(), pos);
                    initHalls(date, showtime, hnameList, hidList);
                    ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(Info_Session.this,
                            android.R.layout.simple_spinner_item, hnameList);
                    nameAdapter.setDropDownViewResource(android.R.layout.
                            simple_spinner_dropdown_item);
                    hallSpinner.setAdapter(nameAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        timeLayout.addView(timeText);
        timeLayout.addView(timeSpinner);

        LinearLayout hallLayout = new LinearLayout(this);
        hallLayout.setOrientation(LinearLayout.HORIZONTAL);
        hallLayout.setLayoutParams(params);
        TextView hallText = new TextView(this);
        hallText.setText("选择影厅");
        hallText.setTextSize(18);


        hallSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    chooseHidList.add(hidList.get(i - 1));
                    Hall chooseHall = allhallList.get(i - 1);
                    String state = "";
                    for (int m = 0; m < chooseHall.getRow(); m++) {
                        for (int n = 0; n < chooseHall.getColumn(); n++) {
                            state += "0";
                        }
                        state += ",";
                    }
                    choosestateList.add(state);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        hallLayout.addView(hallText);
        hallLayout.addView(hallSpinner);

        LinearLayout priceLayout = new LinearLayout(this);
        priceLayout.setLayoutParams(params);
        priceLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView priceText = new TextView(this);
        priceText.setText("影票价格");
        priceText.setTextSize(20);
        EditText priceEdit = new EditText(this);
        priceEdit.setLayoutParams(params);
        priceEditList.add(priceEdit);
        priceLayout.addView(priceText);
        priceLayout.addView(priceEdit);

        layout.addView(timeLayout);
        layout.addView(hallLayout);
        layout.addView(priceLayout);
    }

    private void delSession() {
        if (layoutList.size() > 0) {
            layoutList.remove(layoutList.size() - 1);
            String choosetime = choosetimeList.get(choosetimeList.size() - 1);
            choosetimeList.remove(choosetime);
            allTimeList.add(choosetime);

            allhallList.add(temphallList.get(temphallList.size() - 1));
            temphallList.remove(temphallList.size() - 1);

            int choosehid = chooseHidList.get(chooseHidList.size() - 1);
            chooseHidList.remove(choosehid);

            String choosestate = choosestateList.get(choosestateList.size() - 1);
            choosestateList.remove(choosestate);

            priceEditList.remove(priceEditList.size() - 1);
        } else {
            Toast.makeText(this, "不能再删除啦", Toast.LENGTH_SHORT).show();
        }
    }

    private void initHall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HallRepository hallRepository = new HallRepository();
                allhallList.addAll(hallRepository.findAllHall(cid));
                allhnameList.add("请选择影厅");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Hall hall : allhallList) {
                            allhnameList.add(hall.getHname());
                            allhidList.add(hall.getHid());
                            nameAdapter = new ArrayAdapter<String>(Info_Session.this,
                                    android.R.layout.simple_spinner_item, allhnameList);
                            nameAdapter.setDropDownViewResource(android.R.layout.
                                    simple_spinner_dropdown_item);
                            hallnameSpinner.setAdapter(nameAdapter);
                        }
                    }
                });
            }
        }).start();
    }

    private void initHalls(Date date, String time, List<String> hnameList, List<Integer> hidList) {
        hnameList.clear();
        initSession(date);
        hidList.clear();

        SimpleDateFormat format = new SimpleDateFormat("hh:mm");
        ParsePosition pos = new ParsePosition(0);
        Date timeDate = format.parse(time, pos);

        for (Session session : sessionList) {
            if ((timeDate.getTime() - session.getShowTime().getTime()) > 0 &&
                    (timeDate.getTime() - session.getEndTime().getTime()) < 0) {
                hidList.add(session.getHid());
            }
        }
        for (int hid : hidList) {
            for (Hall hall : allhallList) {
                if (hall.getHid() == hid) {
                    allhallList.remove(hall);
                    temphallList.add(hall);
                    break;
                }
            }
        }

        hidList.clear();

        hnameList.add("请选择影厅");

        for (Hall hall : allhallList) {
            hidList.add(hall.getHid());
            hnameList.add(hall.getHname());
        }
    }

    private void initSession(Date date) {

        for (int i = 0; i < sessionList.size(); i++) {
            Session session = sessionList.get(i);
            if (session.getShowDate().getTime() != date.getTime()) {
                sessionList.remove(i);
                i--;
            }
        }
    }

    private void initSession() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SessionRepository sessionRepository = new SessionRepository();
                session_update = sessionRepository.getSessionBySid(sid);
                sessionList = sessionRepository.findAllSession(cid, session_update.getMid());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
        }).start();
    }

    private void initTimes() {
        allTimeList.add("请选择放映时间");
        allTimeList.add("0:00");
        allTimeList.add("3:00");
        allTimeList.add("6:00");
        allTimeList.add("9:00");
        allTimeList.add("12:00");
        allTimeList.add("15:00");
        allTimeList.add("18:00");
        allTimeList.add("21:00");
        SimpleDateFormat format1 = new SimpleDateFormat("hh:mm");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    }

    private void submit_addSession() {
        List<Session> addSessionList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < choosetimeList.size(); i++) {
            ParsePosition pos = new ParsePosition(0);
            Date sDate = format.parse(choosetimeList.get(i), pos);
            Date dDate = new Date(sDate.getTime() + movie.getMlong() * 60 * 1000);
            Session session = new Session(cid, chooseHidList.get(i), movie.getMid(), date,
                    sDate, dDate, Double.valueOf(priceEditList.get(i).getText().toString()),
                    choosestateList.get(i));
            addSessionList.add(session);
        }

        new Thread() {
            @Override
            public void run() {
                SessionRepository sessionRepository = new SessionRepository();
                HallRepository hallRepository = new HallRepository();
                UhRepository uhRepository = new UhRepository();
                int msg = 0;
                for (Session se : addSessionList) {
                    boolean flag = sessionRepository.addSession(se);
                    int sid = sessionRepository.getSessionId(se);

                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                    boolean flag2 = uhRepository.addUh(new Uh(
                            "添加场次{" + movie.getMname() + "} " +
                                    hallRepository.getHnameByHid(se.getHid()) + ":" +
                                    format1.format(se.getShowDate()) + " " +
                                    format2.format(se.getShowTime()),
                            sid, "session", account, getNowDate()
                    ));
                    if (flag && flag2) {
                        msg = 1;
                    }
                }

                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    private void submit_updateSession() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date sDate = format.parse(showtime, pos);
        Date dDate = new Date(sDate.getTime() + movie.getMlong() * 60 * 1000);
        Session session = new Session(session_update.getSid(), session_update.getCid(),
                session_update.getMid(), date, sDate, dDate,
                Double.valueOf(priceEdit.getText().toString()), state);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int msg = 2;
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                SessionRepository sessionRepository = new SessionRepository();
                HallRepository hallRepository = new HallRepository();
                UhRepository uhRepository = new UhRepository();
                boolean flag = sessionRepository.updateSession(session);
                boolean flag2 = uhRepository.addUh(new Uh(
                        "修改场次{" + movie.getMname() + "} " +
                                hallRepository.getHnameByHid(session.getHid()) + ":" +
                                format1.format(session.getShowDate()) + " " +
                                format2.format(session.getShowTime()),
                        session.getSid(), "session", account, getNowDate()));
                if (flag && flag2) {
                    msg = 3;
                }
                hand.sendEmptyMessage(msg);
            }
        }).start();
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

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(Info_Session.this,
                        "添加失败，请检查网络或联系系统管理员！！", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                Toast.makeText(Info_Session.this, "添加成功",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(Info_Session.this, MovieActivity.class);
                intent.putExtra("cid", cid);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            } else if (msg.what == 2) {
                Toast.makeText(Info_Session.this, "更新失败，请检查网络或联系系统管理员！！",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 3) {
                Toast.makeText(Info_Session.this, "更新成功",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(Info_Session.this, MovieActivity.class);
                intent.putExtra("cid", cid);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        }
    };
}