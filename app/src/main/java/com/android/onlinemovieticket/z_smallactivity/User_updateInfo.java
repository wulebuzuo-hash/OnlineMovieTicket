package com.android.onlinemovieticket.z_smallactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.User;
import com.android.onlinemovieticket.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class User_updateInfo extends AppCompatActivity {
    private EditText accountEdit;

    private List<String> question_list;
    private ArrayAdapter<String> question_adapter;

    private Spinner question1Spinner;
    private String question1;
    private EditText answer1Edit;

    private Spinner question2Spinner;
    private String question2;
    private EditText answer2Edit;

    private Button unext;
    private Button uback;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_update_info);
        accountEdit = (EditText) findViewById(R.id.update_account);

        question1Spinner = (Spinner)findViewById(R.id.update_question1);
        answer1Edit = (EditText)findViewById(R.id.update_answer1);

        question2Spinner = (Spinner)findViewById(R.id.update_question2);
        answer2Edit = (EditText)findViewById(R.id.update_answer2);

        unext = (Button) findViewById(R.id.u_next);
        uback = (Button) findViewById(R.id.u_back);
        progressBar = (ProgressBar) findViewById(R.id.update_info_progressbar);
        progressBar.setVisibility(View.GONE);

        String account = getIntent().getStringExtra("account");
        String type = getIntent().getStringExtra("type");
        if(account != null){
            accountEdit.setText(account);
            accountEdit.setEnabled(false);
        }

        question_list = new ArrayList<String>();
        question_list.add("选择一个问题");
        question_list.add("你父亲的名字是");
        question_list.add("你母亲的名字是");
        question_list.add("你曾经一位老师的名字是");

        question_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,question_list);//适配器
        question_adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);//设置样式
        question1Spinner.setAdapter(question_adapter);
        question2Spinner.setAdapter(question_adapter);

        answer1Edit.setVisibility(View.GONE);
        /**
         * 问题1监听
         */
        question1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    question1 = question_list.get(i);
                    answer1Edit.setVisibility(View.VISIBLE);
                }else {
                    answer1Edit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                answer1Edit.setVisibility(View.GONE);
            }
        });
        answer2Edit.setVisibility(View.GONE);
        /**
         * 问题2监听
         */
        question2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //选择了不同的选项，调用这个
                if(i != 0){
                    question2 = question_list.get(i);
                    answer2Edit.setVisibility(View.VISIBLE);
                }else {
                    answer2Edit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //在下拉选项种选择了本来选的东西，也就是说没改变选项，调用这个
                answer2Edit.setVisibility(View.GONE);
            }
        });

        unext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String answer1 = answer1Edit.getText().toString();
                String answer2 = answer2Edit.getText().toString();

                updateInfo(account, question1,answer1,question2,answer2,type);
            }
        });

        uback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_updateInfo.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 更新用户信息
     * @param account
     * @param question1
     * @param answer1
     * @param question2
     * @param answer2
     * @param type
     */
    private void updateInfo(String account, String question1, String answer1,
                            String question2, String answer2, String type) {
        new Thread(){
            @Override
            public void run() {
                int msg = 0;
                if(type.equals("user")){
                    User user = new User(accountEdit.getText().toString(),
                            question1,answer1,question2,answer2);
                    UserRepository userRepository = new UserRepository();
                    boolean flag = userRepository.updateUser(user);
                    if(flag){
                        msg = 1;
                    }
                }
                hand.sendEmptyMessage(msg);
            }
        }.start();
    }


    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "编辑失败，请检查网络",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "编辑成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(User_updateInfo.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}