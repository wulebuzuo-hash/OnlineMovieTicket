package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.repository.HallRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class List_Hall extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;

    private EditText searchEdit;
    private Button searchButton;

    private Button addHall;
    private ProgressBar progressBar;

    private List<Hall> hallList = new ArrayList<>();
    private ListView hallView;
    private MyAdapter myAdapter;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private String account;
    private String type;
    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_hall);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = (TextView) findViewById(R.id.title_name);
        titlename.setText("影厅列表");

        searchEdit = (EditText) findViewById(R.id.list_hall_searchEdit);
        searchButton = (Button) findViewById(R.id.list_hall_searshButton);
        searchButton.setOnClickListener(this);

        addHall = (Button) findViewById(R.id.list_hall_addHall);
        addHall.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.list_hall_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        bottom_1 = (RadioButton) findViewById(R.id.bottom_choose_movie);
        bottom_1.setOnClickListener(this);
        bottom_2 = (RadioButton) findViewById(R.id.bottom_choose_cinema);
        bottom_2.setText("放映厅");
        bottom_2.setOnClickListener(this);
        bottom_3 = (RadioButton) findViewById(R.id.bottom_choose_my);
        bottom_3.setText("管理历史");
        bottom_3.setOnClickListener(this);
        setBounds(R.drawable.pc_movie,bottom_1);
        setBounds(R.drawable.pc_hall,bottom_2);
        setBounds(R.drawable.my,bottom_3);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
        cid = getIntent().getIntExtra("cid", 0);

        hallView = (ListView) findViewById(R.id.list_hall_hallView);
        myAdapter = new MyAdapter();
        hallView.setAdapter(myAdapter);
        initHalls();
        hallView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(List_Hall.this, Update_Hall.class);
                intent.putExtra("hid", hallList.get(position).getHid());
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                Bundle bundle = new Bundle();
                bundle.putSerializable("hall", hallList.get(position));
                bundle.putSerializable("hallList", (Serializable) hallList);
                intent.putExtras(bundle);
                startActivity(intent);
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
            case R.id.bottom_choose_movie:
                Intent intent = new Intent(List_Hall.this, MovieActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("cid", cid);
                startActivity(intent);
                finish();
                break;
            case R.id.list_hall_searshButton:
                String search = searchEdit.getText().toString();
                if (search.equals("")) {
                    Toast.makeText(List_Hall.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchHall(search);
                }
                break;
            case R.id.list_hall_addHall:
                Intent intent1 = new Intent(List_Hall.this, Info_Hall.class);
                intent1.putExtra("account", account);
                intent1.putExtra("type", type);
                intent1.putExtra("cid", cid);
                startActivity(intent1);
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = new Intent(List_Hall.this, List_Hall.class);
                intent3.putExtra("cid", cid);
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = new Intent(List_Hall.this, List_Uh.class);
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

    private void initHalls() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                HallRepository hallRepository = new HallRepository();
                hallList = hallRepository.findAllHall(cid);
                if (hallList.size() == 0) {
                    msg = 1;
                }

                hand.sendEmptyMessage(msg);

            }
        }.start();
    }

    private void searchHall(String search) {
        List<Hall> tempList = new ArrayList<>();
        for (int i = 0; i < hallList.size(); i++) {
            if (hallList.get(i).getHname().contains(search)) {
                tempList.add(hallList.get(i));
            }
        }
        if(tempList.size()==0){
            Toast.makeText(List_Hall.this, "没有搜索到相关信息", Toast.LENGTH_SHORT).show();
        }else {
            hallList.clear();
            hallList.addAll(tempList);
            myAdapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.GONE);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return hallList.size();
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
                view1 = inflater.inflate(R.layout.item_hall, null);
            } else view1 = view;

            TextView name = (TextView) view1.findViewById(R.id.item_hall_name);
            TextView scale = (TextView) view1.findViewById(R.id.item_hall_scale);

            Hall hall = hallList.get(i);

            name.setText(hall.getHname());
            scale.setText(hall.getRow() + "行" + hall.getColumn() + "列");

            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if(msg.what == 0){
                myAdapter.notifyDataSetChanged();
            }else if (msg.what == 1) {
                Toast.makeText(List_Hall.this, "没有找到放映厅", Toast.LENGTH_SHORT).show();
            }

        }
    };
}