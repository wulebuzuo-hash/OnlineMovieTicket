package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.adapter.HallAdapter;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Uh;
import com.android.onlinemovieticket.repository.HallRepository;
import com.android.onlinemovieticket.repository.UhRepository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Update_Hall extends AppCompatActivity implements View.OnClickListener {
    private Button navButton;
    private TextView titleName;

    private EditText hallNameEdit;
    private EditText rowEdit;
    private EditText colEdit;
    private Button prevButton;

    private ProgressBar progressBar;
    private Button updateButton;

    private RecyclerView hallView;
    private HallAdapter adapter;
    private List<String> seatList = new ArrayList<>();

    private String account;
    private String type;
    private Hall hall;
    private List<Hall> hallList;

    private int cid;
    private int hid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_hall);
        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        hall = (Hall) getIntent().getSerializableExtra("hall");
        hallList = (List<Hall>) getIntent().getSerializableExtra("hallList");
        cid = getIntent().getIntExtra("cid", 0);
        hid = getIntent().getIntExtra("hid", 0);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("编辑电影院");

        hallNameEdit = (EditText) findViewById(R.id.update_hallname);
        rowEdit = (EditText) findViewById(R.id.update_hall_row);
        colEdit = (EditText) findViewById(R.id.update_hall_column);

        prevButton = (Button) findViewById(R.id.update_hall_preview);
        prevButton.setOnClickListener(this);
        updateButton = (Button) findViewById(R.id.update_hall_submit);
        updateButton.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.update_hall_progressbar);
        progressBar.setVisibility(View.GONE);

        hallView = (RecyclerView) findViewById(R.id.update_hall_view);
        adapter = new HallAdapter(seatList);

        if(cid != 0 && hid != 0) {
            initHall();
        }else {
            initView();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.update_hall_preview:
                initSeat();
                break;
            case R.id.update_hall_submit:
                progressBar.setVisibility(View.VISIBLE);
                updateHall();
                break;
            default:
                break;
        }
    }

    /**
     * 根据获取到的放映厅信息绘制布局
     */
    private void initView(){
        hallNameEdit.setText(hall.getHname());
        rowEdit.setText(String.valueOf(hall.getRow()));
        colEdit.setText(String.valueOf(hall.getColumn()));
        initSeat();
    }

    /**
     * 根据hid获取放映厅信息，根据cid获取该电影院所有放映厅列表
     */
    private void initHall(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HallRepository hallRepository = new HallRepository();
                hall = hallRepository.getHallByHid(hid);
                hallList = hallRepository.findAllHall(cid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                    }
                });
            }
        }).start();
    }

    /**
     * 绘制座位布局
     */
    private void initSeat() {
        seatList.clear();
        int col = colEdit.getText().toString().equals("") ? 0 :
                Integer.parseInt(colEdit.getText().toString());
        int row = rowEdit.getText().toString().equals("") ? 0 :
                Integer.parseInt(rowEdit.getText().toString());
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                seatList.add("0");
            }
        }
        adapter.notifyDataSetChanged();
        GridLayoutManager layoutManager = new GridLayoutManager(this, col);
        hallView.setLayoutManager(layoutManager);
        hallView.setAdapter(adapter);
    }

    /**
     * 更新放映厅信息
     */
    private void updateHall() {
        final String hname = hallNameEdit.getText().toString();
        int count = 0;
        for (Hall hall : hallList) {
            if(hall.getHname().equals(hname)) {
                count++;
                if(count > 1) {
                    Toast.makeText(this, "该影厅名已存在", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        final int row = rowEdit.getText().toString().equals("") ? 0 :
                Integer.parseInt(rowEdit.getText().toString());
        final int col = colEdit.getText().toString().equals("") ? 0 :
                Integer.parseInt(colEdit.getText().toString());
        if (row == 0 || col == 0) {
            Toast.makeText(this, "请输入正确的座位数", Toast.LENGTH_SHORT).show();
            return;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int msg = 0;
                    HallRepository hallRepository = new HallRepository();
                    boolean flag = hallRepository.updateHall(new Hall(hall.getHid(),
                            hall.getCid(), hname, row, col));

                    UhRepository uhRepository = new UhRepository();
                    boolean flag2 = uhRepository.addUh(new Uh(hall.getHid(),
                            hall.getHname().equals(hname)?
                    "放映厅{" + hall.getHname() + "}规模改为" + row + "行" + col + "列" :
                                    "放映厅{" + hall.getHname() + "}改为{"+hname+"}，规模为"
                                            + row + "行" + col + "列",
                            hall.getHid(), "hall", account, getNowDate()));
                    if (flag && flag2) {
                        msg = 1;
                    }
                    hand.sendEmptyMessage(msg);
                }
            }).start();
        }
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

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(Update_Hall.this,
                        "更改失败，请检查网络或联系系统管理员！！", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(Update_Hall.this, "更改成功",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Update_Hall.this, List_Hall.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("cid", hall.getCid());
                startActivity(intent);
                finish();
            }
        }
    };
}