package com.android.onlinemovieticket.z_smallactivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Admin;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.repository.AdminRepository;
import com.android.onlinemovieticket.repository.CinemaRepository;

import java.util.ArrayList;
import java.util.List;

public class Info_Admin extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private LinearLayout layout;
    private List<LinearLayout> layoutList = new ArrayList<>();
    private List<EditText> accountEditList = new ArrayList<>();

    private List<Cinema> cinemaList = new ArrayList<>();

    private TextView chooseCname;
    private TextView chooseCid;
    private Button chooseCnameButton;

    private EditText accountEdit;

    private Button addButton;
    private Button delButton;

    private Button submit;
    private ProgressBar progressBar;

    private String account;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_admin);
        navButton = (Button) findViewById(R.id.nav_button);
        navButton.setOnClickListener(this);
        titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("管理员");

        chooseCname = (TextView) findViewById(R.id.info_admin_cinema);
        chooseCid = (TextView) findViewById(R.id.info_admin_cid);
        chooseCnameButton = (Button) findViewById(R.id.info_admin_cinema_btn);
        chooseCnameButton.setOnClickListener(this);
        addButton = (Button) findViewById(R.id.info_admin_add);
        addButton.setOnClickListener(this);
        delButton = (Button) findViewById(R.id.info_admin_del);
        delButton.setOnClickListener(this);

        accountEdit = (EditText) findViewById(R.id.info_admin_account);

        submit = (Button) findViewById(R.id.info_admin_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_admin_progressbar);
        progressBar.setVisibility(View.GONE);

        loadCinema();
        layout = (LinearLayout) findViewById(R.id.info_admin_layout);
        layoutList.add(layout);
        accountEditList.add(accountEdit);

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                backConfirm();
                break;
            case R.id.info_admin_submit:
                progressBar.setVisibility(View.VISIBLE);
                submit_add();
                break;
            case R.id.info_admin_cinema_btn:
                loadCinema();
                break;
            case R.id.info_admin_add:
                addAdminLayout();
                break;
            case R.id.info_admin_del:
                delAdminLayout();
                break;
            default:
                break;
        }
    }

    private void loadCinema() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CinemaRepository cinemaRepository = new CinemaRepository();
                cinemaList = cinemaRepository.findAllCinema();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chooseCinema();
                    }
                });
            }
        }).start();
    }

    private void chooseCinema() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择影院");
        String[] cinemaNames = new String[cinemaList.size()];
        for (int i = 0; i < cinemaList.size(); i++) {
            cinemaNames[i] = cinemaList.get(i).getCname();
        }

        builder.setItems(cinemaNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                chooseCid.setText(cinemaList.get(i).getCid() + " {请管理员牢记}");
                chooseCname.setText(cinemaList.get(i).getCname());
            }
        });
        builder.show();
    }

    private void backConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("返回首页？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Info_Admin.this, MovieActivity.class);
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

    private void addAdminLayout() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                MATCH_PARENT, WRAP_CONTENT);

        LinearLayout linearLayout1 = new LinearLayout(this);
        linearLayout1.setLayoutParams(params);
        linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
        TextView accountText = new TextView(this);
        accountText.setText("管理员" + (accountEditList.size()+1) + "：");
        accountText.setTextSize(18);
        EditText accountEdit = new EditText(this);
        accountEdit.setLayoutParams(params);
        accountEdit.setHint("必填,长度为6-20位");
        accountEditList.add(accountEdit);
        linearLayout1.addView(accountText);
        linearLayout1.addView(accountEdit);

        layout.addView(linearLayout1);
        layoutList.add(linearLayout1);
    }

    private void delAdminLayout() {
        if (layoutList.size() > 1) {
            layout.removeView(layoutList.get(layoutList.size() - 1));
            layoutList.remove(layoutList.size() - 1);
            accountEditList.remove(accountEditList.size() - 1);
        } else {
            Toast.makeText(this, "至少保留一个管理员", Toast.LENGTH_SHORT).show();
        }
    }

    private void submit_add() {

        List<String> accountList = new ArrayList<>();
        for (int i = 0; i < accountEditList.size(); i++) {

            if (accountEditList.get(i).getText().toString().equals("")) {
                Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }  else if (accountEditList.get(i).getText().toString().length() < 6 ||
                    accountEditList.get(i).getText().toString().length() > 20) {
                Toast.makeText(this, "账号长度为6-20位", Toast.LENGTH_SHORT).show();
                return;
            } else if (chooseCid.equals("")) {
                Toast.makeText(this, "请选择影院", Toast.LENGTH_SHORT).show();
                return;
            } else {
                accountList.add(accountEditList.get(i).getText().toString());
            }
        }


        new Thread() {
            @Override
            public void run() {
                AdminRepository adminRepository = new AdminRepository();
                int cid = chooseCid.getText().toString().equals("") ? 0 :
                        Integer.parseInt(chooseCid.getText().toString().split(" ")[0]);
                boolean flag = true;
                int index = 0;

                for (int i = 0; i < accountList.size(); i++) {
                    if (adminRepository.findAdmin(accountList.get(i), cid) != null) {
                        flag = false;
                        index = i;
                        break;
                    }
                }

                if (flag) {
                    boolean flag1 = true;
                    for (int i = 0; i < accountList.size(); i++) {
                        Admin admin = new Admin(accountList.get(i), accountList.get(i), cid);
                        flag1 = adminRepository.addAdmin(admin);
                    }
                    boolean finalFlag = flag1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalFlag) {
                                Toast.makeText(Info_Admin.this,
                                        "添加成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Info_Admin.this, List_Admin.class);
                                intent.putExtra("account", account);
                                intent.putExtra("type", type);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(Info_Admin.this,
                                        "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    int finalIndex = index;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Info_Admin.this,
                                    accountList.get(finalIndex) + "账号已存在,请修改后重新提交",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }.start();
    }

}