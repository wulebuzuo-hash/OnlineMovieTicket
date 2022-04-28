package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Uh;
import com.android.onlinemovieticket.repository.UhRepository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class List_Uh extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;
    private EditText searchEdit;
    private Button searchButton;

    private List<Uh> uhList = new ArrayList<>();
    private ListView uhView;
    private MyAdapter myAdapter;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private ProgressBar progressBar;

    private String account;
    private String type;
    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_uh);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);

        navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = findViewById(R.id.title_name);
        titlename.setText("修改历史");

        searchEdit = findViewById(R.id.list_uh_searchEdit);
        searchButton = findViewById(R.id.list_uh_searchButton);
        searchButton.setOnClickListener(this);
        progressBar = findViewById(R.id.list_uh_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        bottom_1 = findViewById(R.id.bottom_choose_movie);
        bottom_2 = findViewById(R.id.bottom_choose_cinema);
        bottom_3 = findViewById(R.id.bottom_choose_my);
        bottom_1.setOnClickListener(this);
        bottom_2.setOnClickListener(this);
        bottom_3.setOnClickListener(this);
        setBounds(R.drawable.pc_movie,bottom_1);
        setBounds(R.drawable.pc_cinema,bottom_2);
        setBounds(R.drawable.my,bottom_3);

        uhView = findViewById(R.id.list_uh_view);
        myAdapter = new MyAdapter();
        uhView.setAdapter(myAdapter);
        initUh();
        uhView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uh uh = uhList.get(i);
                updateAgainConfirm(uh);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                Intent intent = new Intent(List_Uh.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.list_uh_searchButton:
                String search = searchEdit.getText().toString();
                if (search.equals("")) {
                    Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchUh(search);
                }
                break;
            case R.id.bottom_choose_movie:
                Intent intent2 = new Intent(List_Uh.this, MovieActivity.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);
                intent2.putExtra("cid", cid);
                startActivity(intent2);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = new Intent(List_Uh.this, List_Hall.class);
                intent3.putExtra("cid", cid);
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = new Intent(List_Uh.this, List_Uh.class);
                intent4.putExtra("cid", cid);
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

    private void updateAgainConfirm(final Uh uh) {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Uh.this);
        builder.setTitle("提示");
        builder.setMessage("再去编辑？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (uh.getUh_table().equals("session")) {
                    updateSession(uh);
                } else if (uh.getUh_table().equals("hall")) {
                    updateHall(uh);
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

    private void updateSession(Uh uh) {
        String[] sessionDate = uh.getUhcontent().split(":")[1].split(" ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date showtime = sdf.parse(sessionDate[0] + " " + sessionDate[1], pos);

        if (showtime.getTime() > getNowDate().getTime()) {
            Toast.makeText(List_Uh.this, "不能修改已经开始或结束了的场次",
                    Toast.LENGTH_SHORT).show();
        } else {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            ParsePosition pos2 = new ParsePosition(0);
            SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm");
            ParsePosition pos3 = new ParsePosition(0);
            Intent intent = new Intent(List_Uh.this, Info_Session.class);
            intent.putExtra("account", account);
            intent.putExtra("type", type);
            intent.putExtra("cid", cid);
            intent.putExtra("sid", uh.getUh_updateId());
            startActivity(intent);
        }
    }

    private void updateHall(Uh uh) {
        Intent intent = new Intent(List_Uh.this, Update_Hall.class);
        intent.putExtra("account", account);
        intent.putExtra("type", type);
        intent.putExtra("cid", cid);
        intent.putExtra("hid", uh.getUh_updateId());
        startActivity(intent);
    }

    //获取现在时间
    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    private void searchUh(String searchStr) {
        List<Uh> tempList = new ArrayList<>();
        for (int i = 0; i < uhList.size(); i++) {
            if (uhList.get(i).getUhcontent().contains(searchStr)) {
                tempList.add(uhList.get(i));
            }
        }
        if (tempList.size() == 0) {
            Toast.makeText(List_Uh.this, "没有搜索到相关信息", Toast.LENGTH_SHORT).show();
        } else {
            uhList.clear();
            uhList.addAll(tempList);
            myAdapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void initUh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UhRepository uhRepository = new UhRepository();
                uhList = uhRepository.findAllUhByAccount(account);
                if (uhList.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(List_Uh.this,
                                    "暂没有历史记录", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        }).start();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return uhList.size();
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
                view1 = inflater.inflate(R.layout.item_uh, null);
            } else view1 = view;

            TextView text_time = (TextView) view1.findViewById(R.id.item_uh_time);
            TextView text_content = (TextView) view1.findViewById(R.id.item_uh_content);

            Uh uh = uhList.get(i);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
            text_time.setText(dateFormat.format(uh.getUhDate()));
            text_content.setText(uh.getUhcontent());
            return view1;
        }
    }
}