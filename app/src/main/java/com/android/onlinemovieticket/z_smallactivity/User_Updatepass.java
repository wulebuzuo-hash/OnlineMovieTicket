package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.User;
import com.android.onlinemovieticket.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class User_Updatepass extends Fragment {

    private TextView title;
    private EditText accountEdit;
    private List<String> question_list;
    private ArrayAdapter<String> question_adapter;
    private Spinner questionSpinner;
    private String question;
    private EditText answerEdit;

    private EditText passEdit;
    private EditText pass2Edit;
    private Button updatePass;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_updatepass, container, false);
        accountEdit = (EditText) view.findViewById(R.id.upass_account);

        questionSpinner = (Spinner) view.findViewById(R.id.upass_question);
        answerEdit = (EditText) view.findViewById(R.id.upass_answer);

        passEdit = (EditText) view.findViewById(R.id.upass_pass);
        pass2Edit = (EditText) view.findViewById(R.id.upass_pass2);
        updatePass = (Button) view.findViewById(R.id.updatePass);
        progressBar = (ProgressBar) view.findViewById(R.id.update_pass_progressbar);
        progressBar.setVisibility(View.GONE);

        question_list = new ArrayList<>();
        question_list = new ArrayList<String>();
        question_list.add("选择一个问题");
        question_list.add("你父亲的名字是");
        question_list.add("你母亲的名字是");
        question_list.add("你曾经一位老师的名字是");

        question_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, question_list);//适配器
        question_adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);//设置样式
        questionSpinner.setAdapter(question_adapter);

        answerEdit.setVisibility(View.GONE);

        title = (TextView) view.findViewById(R.id.title_name);
        title.setText("更改密码");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        questionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    question = question_list.get(i);
                    answerEdit.setVisibility(View.VISIBLE);
                } else {
                    answerEdit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                answerEdit.setVisibility(View.GONE);
            }
        });

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String account = accountEdit.getText().toString();
                String answer = answerEdit.getText().toString();
                String pass = passEdit.getText().toString();
                String pass2 = pass2Edit.getText().toString();
                updatePassInfo(account, question, answer, pass, pass2);

            }
        });
    }

    /**
     * 更新用户密码
     * @param account
     * @param question
     * @param answer
     * @param pass
     * @param pass2
     */
    private void updatePassInfo(String account, String question, String answer,
                                String pass, String pass2) {
        new Thread() {
            @Override
            public void run() {
                int msg = 0;
                UserRepository userRepository = new UserRepository();

                User uu = userRepository.findUser(account);
                if (uu != null) {

                    if ((uu.getUquestion1().equals(question) && uu.getUanswer1().equals(answer)) ||
                            (uu.getUquestion2().equals(question) && uu.getAnswer2().equals(answer))) {
                        if (pass.equals(pass2)) {
                            if (userRepository.updatePass(account, pass)) {
                                msg = 1;
                            } else msg = 2;
                        } else msg = 3;
                    } else {
                        msg = 4;
                    }
                }

                hand.sendEmptyMessage(msg);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            progressBar.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(getContext(), "查找账号失败，请核对账号", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getContext(), "修改成功", Toast.LENGTH_LONG).show();
                if (getActivity() instanceof LoginActivity) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            } else if (msg.what == 2) {
                Toast.makeText(getContext(), "修改密码失败，请检查网络或联系管理员",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(getContext(), "修改密码失败，两次输入密码不一致！！",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(getContext(), "修改密码失败，问题或答案不正确，请核对问题答案信息",
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}