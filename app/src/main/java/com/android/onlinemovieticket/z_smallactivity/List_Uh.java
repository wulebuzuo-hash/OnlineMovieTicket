package com.android.onlinemovieticket.z_smallactivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Admin;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Uh;
import com.android.onlinemovieticket.fragment.Lay_bottom;
import com.android.onlinemovieticket.repository.AdminRepository;
import com.android.onlinemovieticket.repository.UhRepository;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class List_Uh extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titlename;
    private ImageButton update_btn;

    private EditText searchEdit;
    private Button searchButton;

    private List<Uh> uhList = new ArrayList<>();
    private ListView uhView;
    private MyAdapter myAdapter;

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
        titlename.setText("管理历史");
        update_btn = findViewById(R.id.title_button_add);
        update_btn.setBackground(getResources().getDrawable(R.drawable.pc_more));
        update_btn.setOnClickListener(this);

        searchEdit = findViewById(R.id.list_uh_searchEdit);
        searchButton = findViewById(R.id.list_uh_searchButton);
        searchButton.setOnClickListener(this);
        progressBar = findViewById(R.id.list_uh_progressbar);
        progressBar.setVisibility(View.VISIBLE);

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
        showBottom();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                loginConfirm();
                break;
            case R.id.title_button_add:
                updatepassword_edit();
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
            default:
                break;
        }
    }

    private void updatepassword_edit() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);
        LinearLayout ll1 = new LinearLayout(this);
        ll1.setLayoutParams(params);
        ll1.setOrientation(LinearLayout.VERTICAL);

        final EditText old_passwordEdit1 = new EditText(List_Uh.this);
        old_passwordEdit1.setLayoutParams(params);
        old_passwordEdit1.setHint("请输入原密码");
        old_passwordEdit1.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll1.addView(old_passwordEdit1);

        final EditText old_passwordEdit2 = new EditText(List_Uh.this);
        old_passwordEdit2.setLayoutParams(params);
        old_passwordEdit2.setHint("请再输一次原密码");
        old_passwordEdit2.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll1.addView(old_passwordEdit2);

        final EditText new_passwordEdit = new EditText(List_Uh.this);
        new_passwordEdit.setLayoutParams(params);
        new_passwordEdit.setHint("请输入新密码");
        new_passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll1.addView(new_passwordEdit);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("请输入");
        builder.setView(ll1);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (old_passwordEdit1.getText().toString().equals("") ||
                        old_passwordEdit2.getText().toString().equals("") ||
                        new_passwordEdit.getText().toString().equals("")) {

                    Toast.makeText(List_Uh.this, "修改失败，请输入完整信息！",
                            Toast.LENGTH_SHORT).show();

                } else if (!old_passwordEdit1.getText().toString().equals(
                        old_passwordEdit2.getText().toString())) {
                    Toast.makeText(List_Uh.this, "修改失败，两次输入的密码不一致！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    updatepassword(old_passwordEdit1.getText().toString(),
                            new_passwordEdit.getText().toString());
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

    private void updatepassword(String old_password, String new_password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AdminRepository adminRepository = new AdminRepository();
                Admin admin = adminRepository.findAdmin(account,cid);
                if(admin.getApassword().equals(old_password)){
                    adminRepository.updateAdmin(admin.getAid(),new_password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(List_Uh.this, "修改成功",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(List_Uh.this, "修改失败，原密码错误！",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
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
                Intent intent = new Intent(List_Uh.this, LoginActivity.class);
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

    private void showBottom() {
        FragmentManager manager = getSupportFragmentManager();  //获取FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("type", type);
        bundle.putInt("cid", cid);
        Fragment bottom_fragment = new Lay_bottom();
        bottom_fragment.setArguments(bundle);
        transaction.add(R.id.list_uh_frame, bottom_fragment).commit();
    }

    /**
     * 再去编辑确认弹窗
     *
     * @param uh
     */
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

    /**
     * 去更新场次
     *
     * @param uh
     */
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

    /**
     * 去更新放映厅
     *
     * @param uh
     */
    private void updateHall(Uh uh) {
        Intent intent = new Intent(List_Uh.this, Update_Hall.class);
        intent.putExtra("account", account);
        intent.putExtra("type", type);
        intent.putExtra("cid", cid);
        intent.putExtra("hid", uh.getUh_updateId());
        startActivity(intent);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    private Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    /**
     * 搜索修改历史
     *
     * @param searchStr
     */
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

    /**
     * 从数据库获取数据
     */
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

    /**
     * 修改历史listview适配器
     */
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