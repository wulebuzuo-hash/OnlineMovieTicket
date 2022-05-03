package com.android.onlinemovieticket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.repository.BossRepository;
import com.android.onlinemovieticket.service.TicketNotificate_IntentService;
import com.android.onlinemovieticket.z_smallactivity.List_Admin;
import com.android.onlinemovieticket.z_smallactivity.List_Hall;
import com.android.onlinemovieticket.z_smallactivity.List_Session;
import com.android.onlinemovieticket.z_smallactivity.List_Uh;
import com.android.onlinemovieticket.z_smallactivity.User_Register;
import com.android.onlinemovieticket.repository.AdminRepository;
import com.android.onlinemovieticket.repository.UserRepository;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup juese;
    private String js;

    private EditText accountEdit;
    private EditText passwordEdit;
    private EditText cidEdit;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;

    private Button login;
    private Button forgetPass;
    private Button goRegister;
    private ProgressBar progressBar;

    private TextView title;

    public DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);
        title = (TextView) findViewById(R.id.title_name);
        title.setText("登 录");
        Button nav_button = (Button) findViewById(R.id.nav_button);
        nav_button.setVisibility(View.GONE);
        ImageButton add = (ImageButton) findViewById(R.id.title_button_add);
        add.setVisibility(View.GONE);

        //设置登录角色
        setJuese();

        /**
         *提取账号密码
         */
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        cidEdit = (EditText) findViewById(R.id.login_cid);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        getAccountandPassword();

        login = (Button) findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.login_progressbar);
        progressBar.setVisibility(View.GONE);
        login.setOnClickListener(this);

        goRegister = (Button) findViewById(R.id.login_goregister);
        goRegister.setOnClickListener(this);

        forgetPass = (Button) findViewById(R.id.login_forgetPass);
        drawerLayout = (DrawerLayout) findViewById(R.id.logina_layout);
        forgetPass.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                loginListener();
                break;
            case R.id.login_goregister:
                Intent intent = new Intent(LoginActivity.this, User_Register.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.login_forgetPass:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }

    /**
     * 登录角色模块
     */
    private void setJuese(){
        juese = (RadioGroup) findViewById(R.id.RadioGroup1);
        juese.check(0);
        js = "用户";
        juese.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton RB = (RadioButton) findViewById(i);
                js = RB.getText().toString();
                LinearLayout cidLay = (LinearLayout) findViewById(R.id.login_cidlay);
                if (js.equals("管理员")) {
                    cidLay.setVisibility(View.VISIBLE);
                } else {
                    cidLay.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 获取缓存的账号密码
     */
    private void getAccountandPassword() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            //将账号密码都设置到文本框中
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            js = pref.getString("js", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
            if (js.equals("用户")) {
                juese.check(R.id.用户);
            } else if (js.equals("管理员")) {
                juese.check(R.id.管理员);
            } else if (js.equals("BOSS")) {
                juese.check(R.id.BOSS);
            }
        }
    }

    /**
     * login点击事件
     */
    private void loginListener() {
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (account.equals("") || account == null) {
            Toast.makeText(getApplicationContext(), "请输入用户名！",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (password.equals("") || password == null) {
                Toast.makeText(getApplicationContext(), "请输入密码！",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (js.equals("") || js == null) {
                    Toast.makeText(getApplicationContext(), "请选择登陆角色！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (js.equals("用户")) {
                        userLogin(account, password);
                    } else if (js.equals("管理员")) {
                        adminLogin(account, password, cidEdit.getText().toString());
                    } else if (js.equals("BOSS")) {
                        bossLogin(account, password);
                    }
                }
            }
        }
    }

    /**
     * 从数据库获取用户账号密码，并校验
     * @param account
     * @param password
     */
    private void userLogin(String account, String password) {

        new Thread() {
            @Override
            public void run() {
                UserRepository userRepository = new UserRepository();
                int msg = userRepository.login(account, password);
                hand1.sendEmptyMessage(msg);
            }
        }.start();

    }

    /**
     * 从数据库获取管理员账号密码，并校验
     * @param account
     * @param password
     * @param cid
     */
    private void adminLogin(String account, String password, String cid) {
        new Thread() {
            @Override
            public void run() {
                AdminRepository adminRepository = new AdminRepository();
                int msg = adminRepository.login(account, password, Integer.valueOf(cid));
                hand1.sendEmptyMessage(msg);
            }
        }.start();
    }

    /**
     * 从数据库获取BOSS账号密码，并校验
     * @param account
     * @param password
     */
    private void bossLogin(String account, String password) {
        new Thread() {
            @Override
            public void run() {
                BossRepository bossRepository = new BossRepository();
                int msg = bossRepository.login(account, password);
                hand1.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                editor = pref.edit();
                if (rememberPass.isChecked()) {//检查复选框是否被选中
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.putString("js", js);
                } else {
                    editor.clear();
                }
                editor.apply();

                if (js.equals("用户")) {
                    Intent startIntent = new Intent(LoginActivity.this,
                            TicketNotificate_IntentService.class);
                    startIntent.putExtra("account", account);
                    startIntent.putExtra("type", js);
                    startService(startIntent);  //启动服务
                }

                Intent intent;
                if (!cidEdit.getText().toString().equals("") && js.equals("管理员")) {
                    intent = new Intent(LoginActivity.this, List_Session.class);
                    intent.putExtra("cid", Integer.valueOf(cidEdit.getText().toString()));
                } else {
                    intent = new Intent(LoginActivity.this, MovieActivity.class);
                }
                intent.putExtra("account", account);
                intent.putExtra("type", js);
                startActivity(intent);
                finish();
            } else if (msg.what == 2) {
                Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(getApplicationContext(), "账号不存在", Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(getApplicationContext(), "密码正确，但影院码错误",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

}