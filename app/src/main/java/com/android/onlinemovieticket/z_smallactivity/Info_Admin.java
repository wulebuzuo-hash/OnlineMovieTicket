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
    private List<EditText> passwordEditList = new ArrayList<>();

    private List<Cinema> cinemaList = new ArrayList<>();

    private TextView chooseCname;
    private TextView chooseCid;
    private Button chooseCnameButton;

    private EditText accountEdit;
    private EditText passwordEdit;

    private Button addButton;
    private Button delButton;

    private Button submit;
    private ProgressBar progressBar;

    private String account;
    private String type;
    private Admin admin;

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
        passwordEdit = (EditText) findViewById(R.id.info_admin_password);

        submit = (Button) findViewById(R.id.info_admin_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_admin_progressbar);
        progressBar.setVisibility(View.GONE);

        admin = (Admin) getIntent().getSerializableExtra("admin");
        loadCinema();
        if (admin != null) {
            accountEdit.setText(admin.getAaccount());
            passwordEdit.setText(admin.getApassword());
            chooseCid.setText(admin.getCid()+" {请管理员牢记}");
            addButton.setVisibility(View.GONE);
            delButton.setVisibility(View.GONE);
        } else {
            addButton.setVisibility(View.VISIBLE);
            delButton.setVisibility(View.VISIBLE);
            layout = (LinearLayout) findViewById(R.id.info_admin_layout);
            layoutList.add(layout);
            accountEditList.add(accountEdit);
            passwordEditList.add(passwordEdit);
        }

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

                if (admin == null) {
                    progressBar.setVisibility(View.VISIBLE);
                    submit_add();
                } else {
                    submit_update();
                }
                break;
            case R.id.info_admin_cinema_btn:
                chooseCinema();
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
                        if (admin != null) {
                            for (Cinema cinema : cinemaList) {
                                if (cinema.getCid() == admin.getCid()) {
                                    chooseCname.setText(cinema.getCname());
                                    break;
                                }
                            }
                        }
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
                chooseCid.setText(cinemaList.get(i).getCid()+" {请管理员牢记}");
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
        accountText.setText("账号:");
        accountText.setTextSize(18);
        EditText accountEdit = new EditText(this);
        accountEdit.setLayoutParams(params);
        accountEdit.setHint("必填,长度为6-20位");
        accountEditList.add(accountEdit);
        linearLayout1.addView(accountText);
        linearLayout1.addView(accountEdit);


        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setLayoutParams(params);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        TextView passwordText = new TextView(this);
        passwordText.setText("密码:");
        passwordText.setTextSize(18);
        EditText passwordEdit = new EditText(this);
        passwordEdit.setLayoutParams(params);
        passwordEdit.setHint("必填，长度为6-20位");
        passwordEditList.add(passwordEdit);
        linearLayout2.addView(passwordText);
        linearLayout2.addView(passwordEdit);

        LinearLayout linearLayout3 = new LinearLayout(this);
        linearLayout3.setLayoutParams(params);
        linearLayout3.setOrientation(LinearLayout.VERTICAL);
        linearLayout3.addView(linearLayout1);
        linearLayout3.addView(linearLayout2);

        layout.addView(linearLayout3);
        layoutList.add(linearLayout3);
    }

    private void delAdminLayout() {
        if (layoutList.size() > 1) {
            layout.removeView(layoutList.get(layoutList.size() - 1));
            layoutList.remove(layoutList.size() - 1);
            accountEditList.remove(accountEditList.size() - 1);
            passwordEditList.remove(passwordEditList.size() - 1);
        } else {
            Toast.makeText(this, "至少保留一个管理员", Toast.LENGTH_SHORT).show();
        }
    }

    private void submit_add() {

        List<String> accountList = new ArrayList<>();
        List<String> passwordList = new ArrayList<>();
        for (int i = 0; i < accountEditList.size(); i++) {

            if (accountEditList.get(i).getText().toString().equals("")) {
                Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordEditList.get(i).getText().toString().equals("")) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            } else if (accountEditList.get(i).getText().toString().length() < 6 ||
                    accountEditList.get(i).getText().toString().length() > 20) {
                Toast.makeText(this, "账号长度为6-20位", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordEditList.get(i).getText().toString().length() < 6 ||
                    passwordEditList.get(i).getText().toString().length() > 20) {
                Toast.makeText(this, "密码长度为6-20位", Toast.LENGTH_SHORT).show();
                return;
            } else if (chooseCid.equals("")) {
                Toast.makeText(this, "请选择影院", Toast.LENGTH_SHORT).show();
                return;
            } else {
                accountList.add(accountEditList.get(i).getText().toString());
                passwordList.add(passwordEditList.get(i).getText().toString());
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
                    for (int i = 0; i < accountList.size(); i++) {
                        Admin admin = new Admin(accountList.get(i), passwordList.get(i), cid);
                        adminRepository.addAdmin(admin);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Info_Admin.this,
                                    "添加成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Info_Admin.this, List_Admin.class);
                            intent.putExtra("account", account);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            finish();
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

    private void submit_update() {
        if(accountEdit.getText().toString().length() < 6 || accountEdit.getText().toString().length() > 20) {
            Toast.makeText(this, "账号长度为6-20位", Toast.LENGTH_SHORT).show();
            return;
        }else if(passwordEdit.getText().toString().length() < 6 || passwordEdit.getText().toString().length() > 20) {
            Toast.makeText(this, "密码长度为6-20位", Toast.LENGTH_SHORT).show();
            return;
        }else if(chooseCid.getText().toString().equals("")) {
            Toast.makeText(this, "请选择影院", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread() {
            @Override
            public void run() {
                AdminRepository adminRepository = new AdminRepository();
                int cid = chooseCid.getText().toString().equals("") ? 0 :
                        Integer.parseInt(chooseCid.getText().toString().split(" ")[0]);
                if(!accountEdit.getText().toString().equals(admin.getAaccount()) &&
                        adminRepository.findAdmin(accountEdit.getText().toString(), cid) != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Info_Admin.this,
                                    "账号已存在,请修改后重新提交",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }else {
                    Admin mm = new Admin(admin.getAid(),accountEdit.getText().toString(),
                            passwordEdit.getText().toString(), cid);
                    boolean flag = adminRepository.updateAdmin(mm);
                    if(flag) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Info_Admin.this,
                                        "修改成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Info_Admin.this,
                                        List_Admin.class);
                                intent.putExtra("account", account);
                                intent.putExtra("type", type);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Info_Admin.this,
                                        "修改失败", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }

            }
        }.start();
    }
}