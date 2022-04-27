package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Admin;
import com.android.onlinemovieticket.repository.AdminRepository;

public class Info_Admin extends AppCompatActivity implements View.OnClickListener {

    private Button navButton;
    private TextView titleName;

    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText cidEdit;
    private EditText nameEdit;
    private EditText sexEdit;
    private EditText idcardEdit;
    private EditText callEdit;
    private EditText mailEdit;

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

        accountEdit = (EditText) findViewById(R.id.info_admin_account);
        passwordEdit = (EditText) findViewById(R.id.info_admin_password);
        cidEdit = (EditText) findViewById(R.id.info_admin_cid);
        nameEdit = (EditText) findViewById(R.id.info_admin_name);
        sexEdit = (EditText) findViewById(R.id.info_admin_sex);
        idcardEdit = (EditText) findViewById(R.id.info_admin_idcard);
        callEdit = (EditText) findViewById(R.id.info_admin_call);
        mailEdit = (EditText) findViewById(R.id.info_admin_mail);

        submit = (Button) findViewById(R.id.info_admin_submit);
        submit.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.info_admin_progressbar);
        progressBar.setVisibility(View.GONE);

        Admin admin = (Admin)getIntent().getSerializableExtra("admin");
        if(admin != null){
            accountEdit.setText(admin.getAaccount());
            passwordEdit.setText(admin.getApassword());
            cidEdit.setText(admin.getCid()+"");
            nameEdit.setText(admin.getAname());
            sexEdit.setText(admin.getAsex()+"");
            idcardEdit.setText(admin.getAidCard());
            callEdit.setText(admin.getAcall());
            mailEdit.setText(admin.getAmail());
        }

        account = getIntent().getStringExtra("account");
        type = getIntent().getStringExtra("type");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_button:
                finish();
                break;
            case R.id.info_admin_submit:
                progressBar.setVisibility(View.VISIBLE);
                addAdmin();
                break;
            default:
                break;
        }
    }

    private void addAdmin() {
        new Thread() {
            @Override
            public void run() {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String cidStr = cidEdit.getText().toString();
                String name = nameEdit.getText().toString();
                String sex = sexEdit.getText().toString();
                String idcard = idcardEdit.getText().toString();
                String call = callEdit.getText().toString();
                String mail = mailEdit.getText().toString();
                Admin admin = new Admin(account, password, Integer.valueOf(cidStr), name,
                        Integer.valueOf(sex), idcard, call, mail);
                int msg = 0;
                AdminRepository adminRepository = new AdminRepository();

                Admin aa = adminRepository.findAdmin(account, admin.getCid());
                if(aa != null){
                    msg = 1;
                }else{
                    boolean flag = adminRepository.addAdmin(admin);
                    if (flag) {
                        msg = 2;
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(Info_Admin.this,
                        "添加失败，网络有问题！！", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 1) {
                Toast.makeText(Info_Admin.this, "该影院的账号已经存在，请换一个账号",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            } else if (msg.what == 2) {
                Toast.makeText(Info_Admin.this, "注册成功", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(Info_Admin.this, List_Admin.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                finish();
            }
        }
    };
}