package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.android.onlinemovieticket.fragment.Lay_bottom;
import com.android.onlinemovieticket.repository.AdminRepository;
import com.android.onlinemovieticket.repository.CinemaRepository;

import java.util.ArrayList;
import java.util.List;

public class List_Admin extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;
    private ImageButton addAdmin;

    private EditText searchEdit;
    private Button searchButton;

    private ProgressBar progressBar;

    private List<Admin> allAdminList = new ArrayList<>();
    private List<Admin> showAdminList = new ArrayList<>();
    private List<String> cnameList = new ArrayList<>();
    private List<String> showcnameList = new ArrayList<>();
    private ListView adminView;
    private MyAdapter adapter;

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
        addAdmin = (ImageButton) findViewById(R.id.title_button_add);
        addAdmin.setVisibility(View.VISIBLE);
        addAdmin.setOnClickListener(this);

        searchEdit = (EditText) findViewById(R.id.my_boss_searchEdit);
        searchButton = (Button) findViewById(R.id.my_boss_searshButton);
        searchButton.setOnClickListener(this);

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
        showBottom();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                loginConfirm();
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
            case R.id.title_button_add:
                addConfirm();
                break;
            default:
                break;

        }

    }

    private void addConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(List_Admin.this);
        builder.setTitle("提示");
        builder.setMessage("确定要添加管理员吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(List_Admin.this, Info_Admin.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
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

    /**
     * 返回登录界面确认
     */
    private void loginConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回登录页面？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(List_Admin.this, LoginActivity.class);
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

    private void showBottom(){
        FragmentManager manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);
        Fragment bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.list_admin_frame, bottom_fragment).commit();
    }

    private void initAdmins() {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                AdminRepository adminRepository = new AdminRepository();
                allAdminList = adminRepository.findAllAdmin();
                CinemaRepository cinemaRepository = new CinemaRepository();
                if (allAdminList.size() == 0) {
                    msg = 1;
                }else {

                    for (int i = 0; i < allAdminList.size(); i++) {
                        cnameList.add(cinemaRepository.getCnameByCid(allAdminList.get(i).getCid()));
                    }

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
        showcnameList.clear();
        showAdminList.addAll(allAdminList);
        showcnameList.addAll(cnameList);
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void searchAdmin(final String search){
        showAdminList.clear();
        showcnameList.clear();

        for(int i = 0; i < allAdminList.size(); i++){
            Admin admin = allAdminList.get(i);
            if(admin.getAaccount().contains(search)){
                showAdminList.add(admin);
                showcnameList.add(cnameList.get(i));
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
            cname.setText(showcnameList.get(i));

            return view1;
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(List_Admin.this, "没有管理员", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }
    };

}