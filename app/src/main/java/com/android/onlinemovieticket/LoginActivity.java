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
        title.setText("??? ???");
        Button nav_button = (Button) findViewById(R.id.nav_button);
        nav_button.setVisibility(View.GONE);

        //??????????????????
        setJuese();

        /**
         *??????????????????
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
     * ??????????????????
     */
    private void setJuese(){
        juese = (RadioGroup) findViewById(R.id.RadioGroup1);
        juese.check(0);
        js = "??????";
        juese.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton RB = (RadioButton) findViewById(i);
                js = RB.getText().toString();
                LinearLayout cidLay = (LinearLayout) findViewById(R.id.login_cidlay);
                if (js.equals("?????????")) {
                    cidLay.setVisibility(View.VISIBLE);
                } else {
                    cidLay.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * ???????????????????????????
     */
    private void getAccountandPassword() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            //???????????????????????????????????????
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            js = pref.getString("js", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
            if (js.equals("??????")) {
                juese.check(R.id.??????);
            } else if (js.equals("?????????")) {
                juese.check(R.id.?????????);
            } else if (js.equals("BOSS")) {
                juese.check(R.id.BOSS);
            }
        }
    }

    /**
     * login????????????
     */
    private void loginListener() {
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if (account.equals("") || account == null) {
            Toast.makeText(getApplicationContext(), "?????????????????????",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (password.equals("") || password == null) {
                Toast.makeText(getApplicationContext(), "??????????????????",
                        Toast.LENGTH_SHORT).show();
            } else {
                if (js.equals("") || js == null) {
                    Toast.makeText(getApplicationContext(), "????????????????????????",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    if (js.equals("??????")) {
                        userLogin(account, password);
                    } else if (js.equals("?????????")) {
                        adminLogin(account, password, cidEdit.getText().toString());
                    } else if (js.equals("BOSS")) {
                        bossLogin(account, password);
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????????????????????????????
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
     * ???????????????????????????????????????????????????
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
     * ??????????????????BOSS????????????????????????
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
                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_LONG).show();
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                editor = pref.edit();
                if (rememberPass.isChecked()) {//??????????????????????????????
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account);
                    editor.putString("password", password);
                    editor.putString("js", js);
                } else {
                    editor.clear();
                }
                editor.apply();

                if (js.equals("??????")) {
                    Intent startIntent = new Intent(LoginActivity.this,
                            TicketNotificate_IntentService.class);
                    startIntent.putExtra("account", account);
                    startIntent.putExtra("type", js);
                    startService(startIntent);  //????????????
                }

                Intent intent;
                if (!cidEdit.getText().toString().equals("") && js.equals("?????????")) {
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
                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(getApplicationContext(), "?????????????????????????????????",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

}