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

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Admin;
import com.android.onlinemovieticket.repository.AdminRepository;

import java.util.ArrayList;
import java.util.List;

public class List_Admin extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;

    private EditText searchEdit;
    private Button searchButton;

    private Button addAdmin;
    private ProgressBar progressBar;

    private List<Admin> allAdminList = new ArrayList<>();
    private List<Admin> showAdminList = new ArrayList<>();
    private ListView adminView;
    private MyAdapter adapter;

    private RadioButton bottom_1;
    private RadioButton bottom_2;
    private RadioButton bottom_3;

    private String account;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_admin);

        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titlename = (TextView) findViewById(R.id.title_name);
        titlename.setText("电影院");

        searchEdit = (EditText) findViewById(R.id.my_boss_searchEdit);
        searchButton = (Button) findViewById(R.id.my_boss_searshButton);
        searchButton.setOnClickListener(this);

        bottom_1 = (RadioButton) findViewById(R.id.bottom_choose_movie);
        bottom_1.setOnClickListener(this);
        bottom_2 = (RadioButton) findViewById(R.id.bottom_choose_cinema);
        bottom_2.setOnClickListener(this);
        bottom_3 = (RadioButton) findViewById(R.id.bottom_choose_my);
        bottom_3.setText("管理员");
        bottom_3.setOnClickListener(this);

        setBounds(R.drawable.pc_movie,bottom_1);
        setBounds(R.drawable.pc_hall,bottom_2);
        setBounds(R.drawable.my,bottom_3);

        addAdmin = (Button) findViewById(R.id.list_admin_add);
        addAdmin.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.list_admin_progressbar);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");

        adminView = (ListView) findViewById(R.id.my_boss_adminView);
        adapter = new MyAdapter();
        adminView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);
        initAdmins();   //初始化电影数据

        adminView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Admin admin = showAdminList.get(i);
                Intent intent = new Intent(List_Admin.this, Info_Admin.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                Bundle bundle = new Bundle();
                bundle.putSerializable("admin", admin);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                Intent intent = new Intent(List_Admin.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.my_boss_searshButton:
                String search = searchEdit.getText().toString();
                if (search.equals("")) {
                    Toast.makeText(List_Admin.this,
                            "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchAdmin(search);
                }
                break;
            case R.id.list_admin_add:
                Intent intent1 = new Intent(List_Admin.this, Info_Admin.class);
                intent1.putExtra("account", account);
                intent1.putExtra("type", type);
                startActivity(intent1);
                break;
            case R.id.bottom_choose_movie:
                Intent intent2 = new Intent(List_Admin.this, MovieActivity.class);
                intent2.putExtra("account", account);
                intent2.putExtra("type", type);
                startActivity(intent2);
                finish();
                break;
            case R.id.bottom_choose_cinema:
                Intent intent3 = new Intent(List_Admin.this, CinemaActivity.class);
                intent3.putExtra("account", account);
                intent3.putExtra("type", type);
                startActivity(intent3);
                finish();
                break;
            case R.id.bottom_choose_my:
                Intent intent4 = null;
                if (type.equals("用户")) {
                    intent4 = new Intent(List_Admin.this, My_User.class);
                } else if (type.equals("管理员")) {
                    intent4 = new Intent(List_Admin.this, List_Uh.class);
                } else if (type.equals("BOSS")) {
                    intent4 = new Intent(List_Admin.this, List_Admin.class);
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

    private void initAdmins() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                AdminRepository adminRepository = new AdminRepository();
                allAdminList = adminRepository.findAllAdmin();
                if (allAdminList.size() == 0) {
                    msg = 1;
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initAdmin_all();
                        }
                    });
                }

                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    private void initAdmin_all(){
        showAdminList.clear();
        showAdminList.addAll(allAdminList);
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void searchAdmin(final String search){
        showAdminList.clear();
        for(Admin admin : allAdminList){
            if(admin.getAaccount().contains(search)){
                showAdminList.add(admin);
            }
        }
        if(showAdminList.size() == 0){
            Toast.makeText(List_Admin.this,
                    "搜索失败，请核对管理员名", Toast.LENGTH_LONG).show();
            initAdmin_all();
        }else {
            progressBar.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return showAdminList.size();
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
                view1 = inflater.inflate(R.layout.item_admin, null);
            } else view1 = view;

            TextView account = (TextView) view1.findViewById(R.id.item_admin_account);
            TextView cname = (TextView) view1.findViewById(R.id.item_admin_cname);

            Admin admin = showAdminList.get(i);

            account.setText(admin.getAaccount());
            cname.setText(admin.getCid()+"");

            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
            } else if (msg.what == 1) {
                Toast.makeText(List_Admin.this, "没有管理员", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    };

}