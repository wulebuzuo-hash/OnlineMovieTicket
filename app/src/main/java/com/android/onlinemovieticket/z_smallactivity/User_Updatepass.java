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
        question_list.add("??????????????????");
        question_list.add("?????????????????????");
        question_list.add("?????????????????????");
        question_list.add("?????????????????????????????????");

        question_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, question_list);//?????????
        question_adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);//????????????
        questionSpinner.setAdapter(question_adapter);

        answerEdit.setVisibility(View.GONE);

        title = (TextView) view.findViewById(R.id.title_name);
        title.setText("????????????");
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

                if(account.length() >= 6 && account.length() <= 20){
                    if(pass.length() >= 6 && pass.length() <= 20){
                        updatePassInfo(account, question, answer, pass, pass2);
                    }else {
                        Toast.makeText(getActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    /**
     * ??????????????????
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
                Toast.makeText(getContext(), "????????????????????????????????????", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getContext(), "????????????", Toast.LENGTH_LONG).show();

            } else if (msg.what == 2) {
                Toast.makeText(getContext(), "??????????????????????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 3) {
                Toast.makeText(getContext(), "??????????????????????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
            } else if (msg.what == 4) {
                Toast.makeText(getContext(), "???????????????????????????????????????????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
            }
        }
    };
}