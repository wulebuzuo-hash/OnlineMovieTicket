package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.Nullable;
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

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.User;
import com.android.onlinemovieticket.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class User_Register extends AppCompatActivity {

    private EditText uaccountEdit;
    private EditText upasswordEdit;
    private EditText upassword2Edit;

    private List<String> question_list;
    private ArrayAdapter<String> question_adapter;
    private Spinner question1Spinner;
    private String question1;
    private EditText answer1Edit;
    private Spinner question2Spinner;
    private String question2;
    private EditText answer2Edit;

    private ProgressBar progressBar;
    private Button register;
    private Button rback;
    private Button rnext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);
        uaccountEdit = (EditText) findViewById(R.id.uaccount);
        upasswordEdit = (EditText) findViewById(R.id.upassword);
        upassword2Edit = (EditText) findViewById(R.id.upassword2);

        question1Spinner = (Spinner) findViewById(R.id.spinner_question1);
        answer1Edit = (EditText) findViewById(R.id.answer1);
        question2Spinner = (Spinner) findViewById(R.id.spinner_question2);
        answer2Edit = (EditText) findViewById(R.id.answer2);


        rback = (Button) findViewById(R.id.r_back);
        rnext = (Button) findViewById(R.id.r_next);
        rnext.setVisibility(View.GONE);
        register = (Button) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.register_progressbar);
        progressBar.setVisibility(View.GONE);

        question_list = new ArrayList<String>();
        question_list.add("?????????????????????");
        question_list.add("?????????????????????");
        question_list.add("?????????????????????????????????");


        question_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, question_list);//?????????
        question_adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);//????????????
        question1Spinner.setAdapter(question_adapter);
        question2Spinner.setAdapter(question_adapter);
        answer1Edit.setVisibility(View.GONE);
        /**
         * ??????1??????
         */
        question1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                question1 = question_list.get(i);
                answer1Edit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                answer1Edit.setVisibility(View.GONE);
            }
        });
        answer2Edit.setVisibility(View.GONE);
        /**
         * ??????2??????
         */
        question2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //???????????????????????????????????????
                if (i != 0) {
                    question2 = question_list.get(i);
                    answer2Edit.setVisibility(View.VISIBLE);
                } else {
                    answer2Edit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //??????????????????????????????????????????????????????????????????????????????????????????
                answer2Edit.setVisibility(View.GONE);
            }
        });

        /**
         * ??????????????????
         */
        rback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        /**
         * ??????????????????????????????????????????
         */
        rnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Register.this, User_updateInfo.class);
                intent.putExtra("account", uaccountEdit.getText().toString());
                intent.putExtra("type", "user");
                startActivityForResult(intent, 1);
                finish();
            }
        });

        /**
         * ??????????????????
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String account = uaccountEdit.getText().toString();
                String password = upasswordEdit.getText().toString();
                String upassword2 = upassword2Edit.getText().toString();
                String answer1 = answer1Edit.getText().toString();
                String answer2 = answer2Edit.getText().toString();

                if (account.length() >= 6 && account.length() <= 20) {
                    if (password.length() >= 6 && password.length() <= 20) {
                        User user = new User(account, password,
                                question1, answer1, question2, answer2);

                        new Thread() {
                            @Override
                            public void run() {
                                int msg = 0;
                                if (password.equals(upassword2)) {
                                    if (!((question1 == null) || (question2 == null)
                                            || (question1.equals(question2)))) {
                                        UserRepository userRepository = new UserRepository();
                                        User uu = userRepository.findUser(user.getUaccount());
                                        if (uu != null) {
                                            msg = 1;
                                        } else {
                                            boolean flag = userRepository.addUser(user);
                                            if (flag) {
                                                msg = 2;
                                            }
                                        }
                                    } else {
                                        msg = 3;
                                    }
                                }
                                hand.sendEmptyMessage(msg);
                            }
                        }.start();
                    }else {
                        Toast.makeText(User_Register.this,
                                "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(User_Register.this,
                            "???????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(User_Register.this,
                        "????????????,?????????????????????????????????", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(User_Register.this, "??????????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 2) {
                Toast.makeText(User_Register.this, "????????????", Toast.LENGTH_LONG).show();
                register.setVisibility(View.GONE);
                rnext.setVisibility(View.VISIBLE);
            } else if (msg.what == 3) {
                Toast.makeText(User_Register.this,
                        "????????????????????????????????????", Toast.LENGTH_LONG).show();
            }
        }
    };

}