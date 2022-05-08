package com.android.onlinemovieticket.z_smallactivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.fragment.Census_Ticket;
import com.android.onlinemovieticket.fragment.choose_area;
import com.android.onlinemovieticket.repository.CinemaRepository;
import com.android.onlinemovieticket.repository.MovieRepository;

import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Info_Cinema extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private LinearLayout layout;
    private List<LinearLayout> layoutList = new ArrayList<>();
    private List<TextView> nameEditList = new ArrayList<>();
    private List<TextView> positionEditList = new ArrayList<>();
    private List<TextView> callEditList = new ArrayList<>();

    private EditText nameEdit;

    public TextView countryEdit;
    private Button countryButton;
    private EditText positionEdit;
    private EditText callEdit;

    private Button addButton;
    private Button delButton;

    private Button submit;
    private ProgressBar progressBar;

    private String account;
    private String type;
    private Cinema cinema;
    private List<Cinema> cinemaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_cinema);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("电影院");

        nameEdit = (EditText) findViewById(R.id.info_cinema_name);
        countryEdit = (TextView) findViewById(R.id.info_cinema_country);
        countryButton = (Button) findViewById(R.id.info_cinema_country_button);
        countryButton.setOnClickListener(this);
        positionEdit = (EditText) findViewById(R.id.info_cinema_position);
        callEdit = (EditText) findViewById(R.id.info_cinema_call);
        addButton = (Button) findViewById(R.id.info_cinema_add);
        addButton.setOnClickListener(this);
        delButton = (Button) findViewById(R.id.info_cinema_del);
        delButton.setOnClickListener(this);

        submit = (Button) findViewById(R.id.info_cinema_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_cinema_progressbar);
        progressBar.setVisibility(View.GONE);

        cinema = (Cinema) getIntent().getSerializableExtra("cinema");
        if (cinema != null) {
            nameEdit.setText(cinema.getCname());
            String[] address = cinema.getCposition().split("区");
            countryEdit.setText(address[0]+"区");
            positionEdit.setText(address[1]);
            callEdit.setText(cinema.getCcall());
            addButton.setVisibility(View.GONE);
            delButton.setVisibility(View.GONE);
        } else {
            layout = (LinearLayout) findViewById(R.id.info_cinema_layout);
            nameEditList.add(nameEdit);
            positionEditList.add(positionEdit);
            callEditList.add(callEdit);
            addButton.setVisibility(View.VISIBLE);
            delButton.setVisibility(View.VISIBLE);
        }

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cinemaList = (List<Cinema>) getIntent().getSerializableExtra("cinemaList");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                backConfirm();
                break;
            case R.id.info_cinema_add:
                addCinemaLayout();
                break;
            case R.id.info_cinema_del:
                delCinemaLayout();
                break;
            case R.id.info_cinema_submit:
                progressBar.setVisibility(View.VISIBLE);
                if (cinema != null) {
                    update_submit(nameEdit.getText().toString(), positionEdit.getText().toString(),
                            callEdit.getText().toString());
                } else {
                    add_submit();
                }
                break;
            case R.id.info_cinema_country_button:
                outDrawer_choose();
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void outDrawer_choose() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        choose_area area = new choose_area();
        ft.add(R.id.info_cinema_drawer_frame, area).commit();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.info_cinema_drawer);
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void delDrawer_choose() {

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.info_cinema_drawer);
        drawerLayout.closeDrawer(GravityCompat.END);
    }

    private void backConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回首页？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Info_Cinema.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
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

    private void addCinemaLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);

        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setLayoutParams(params);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        TextView nameText = new TextView(this);
        nameText.setText("电影院名:");
        nameText.setTextSize(18);
        EditText nameEdit = new EditText(this);
        nameEdit.setLayoutParams(params);
        nameEditList.add(nameEdit);
        linearLayout1.addView(nameText);
        linearLayout1.addView(nameEdit);


        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setLayoutParams(params);
        linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
        TextView positionText2 = new TextView(this);
        positionText2.setText("地理位置");
        positionText2.setTextSize(18);
        EditText positionEdit2 = new EditText(this);
        positionEdit.setLayoutParams(params);
        positionEditList.add(positionEdit2);
        linearLayout3.addView(positionText2);
        linearLayout3.addView(positionEdit2);

        LinearLayout linearLayout4 = new LinearLayout(this);
        linearLayout4.setLayoutParams(params);
        linearLayout4.setOrientation(LinearLayout.HORIZONTAL);
        TextView callText = new TextView(this);
        callText.setText("联系电话");
        callText.setTextSize(18);
        EditText callEdit = new EditText(this);
        callEdit.setLayoutParams(params);
        callEditList.add(callEdit);
        linearLayout4.addView(callText);
        linearLayout4.addView(callEdit);

        LinearLayout linearLayout5 = new LinearLayout(this);
        linearLayout5.setLayoutParams(params);
        linearLayout5.setOrientation(LinearLayout.VERTICAL);
        linearLayout5.addView(linearLayout1);
        linearLayout5.addView(linearLayout3);
        linearLayout5.addView(linearLayout4);

        layout.addView(linearLayout5);
        layoutList.add(linearLayout5);
    }

    private void delCinemaLayout() {
        if (layoutList.size() > 0) {
            layout.removeView(layoutList.get(layoutList.size() - 1));
            nameEditList.remove(nameEditList.size() - 1);
            positionEditList.remove(positionEditList.size() - 1);
            callEditList.remove(callEditList.size() - 1);
            layoutList.remove(layoutList.size() - 1);
        } else {
            Toast.makeText(this, "不能再删除啦", Toast.LENGTH_SHORT).show();
        }
    }

    private void update_submit(String cname, String position, String call) {
        boolean flag = true;
        if (!cname.equals(cinema.getCname())) {
            for (Cinema ci : cinemaList) {
                if (ci.getCname().equals(cname)) {
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int msg = 0;
                    CinemaRepository cinemaRepository = new CinemaRepository();
                    boolean flag = cinemaRepository.updateCinema(new Cinema(
                            cinema.getCid(), cname, countryEdit.getText() + position, call));
                    if (flag) {
                        msg = 1;
                    }
                    hand.sendEmptyMessage(msg);
                }
            }).start();
        } else {
            Toast.makeText(this, "影院名已存在", Toast.LENGTH_SHORT).show();
        }

    }

    private void add_submit() {
        List<String> nameList = new ArrayList<>(nameEditList.size());
        boolean flag = true;
        for (int i = 0; i < nameEditList.size(); i++) {
            String name = nameEditList.get(i).getText().toString();
            for (Cinema cinema : cinemaList) {
                if (cinema.getCname().equals(name)) {
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                break;
            }
            nameList.add(nameEditList.get(i).getText().toString());
        }
        if (flag) {
            new Thread() {
                @Override
                public void run() {
                    int msg = 2;

                    for (int i = 0; i < nameList.size(); i++) {
                        String name = nameList.get(i);
                        String position = positionEditList.get(i).getText().toString();
                        String call = callEditList.get(i).getText().toString();
                        CinemaRepository cinemaRepository = new CinemaRepository();
                        Cinema cinema = new Cinema(name, position, call);
                        boolean flag = cinemaRepository.addCinema(cinema);
                        if (flag) {
                            msg = 3;
                        }
                    }
                    hand.sendEmptyMessage(msg);
                }
            }.start();
        } else {
            Toast.makeText(this, "影院名已存在", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(Info_Cinema.this,
                        "更改失败，请检查网络或联系系统管理员！！", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Info_Cinema.this, "更改成功",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Info_Cinema.this, CinemaActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            } else if (msg.what == 2) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Info_Cinema.this,
                        "添加失败，请检查网络或联系系统管理员！！", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Info_Cinema.this, "添加成功",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Info_Cinema.this, CinemaActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        }
    };

}