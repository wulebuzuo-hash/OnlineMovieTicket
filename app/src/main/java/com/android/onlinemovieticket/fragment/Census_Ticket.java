package com.android.onlinemovieticket.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.utils.CircleProgressView;
import com.android.onlinemovieticket.z_smallactivity.List_Ticket;

import java.util.List;

public class Census_Ticket extends Fragment {

    private TextView all_num;
    private TextView all_money;

    private CircleProgressView circle_1;
    private CircleProgressView circle_2;
    private CircleProgressView circle_3;
    private TextView progress_1;
    private TextView progress_2;
    private TextView progress_3;
    private TextView text_1;
    private TextView text_2;
    private TextView text_3;

    private TextView movie_name;
    private TextView movie_score;
    private TextView movie_comment;

    private List<String> list;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.census_ticket, container, false);
        list = getArguments().getStringArrayList("list");
        initView(view);
        return view;
    }

    private void initView(View view) {
        all_num = view.findViewById(R.id.census_ticket_all_num);
        all_num.setText(list.get(0));
        all_money = view.findViewById(R.id.census_ticket_all_money);
        all_money.setText(list.get(1));

        text_1 = view.findViewById(R.id.census_ticket_text_1);
        text_1.setText(list.get(2));
        int progress_1_num = Integer.parseInt(list.get(3));
        circle_1 = view.findViewById(R.id.census_ticket_circle_1);
        circle_1.setProgress(progress_1_num);
        progress_1 = view.findViewById(R.id.census_ticket_progress_1);
        progress_1.setText(progress_1_num + "%");


        text_2 = view.findViewById(R.id.census_ticket_text_2);
        text_2.setText(list.get(4));
        int progress_2_num = Integer.parseInt(list.get(5));
        circle_2 = view.findViewById(R.id.census_ticket_circle_2);
        circle_2.setProgress(progress_2_num);
        progress_2 = view.findViewById(R.id.census_ticket_progress_2);
        progress_2.setText(progress_2_num + "%");


        text_3 = view.findViewById(R.id.census_ticket_text_3);
        text_3.setText(list.get(6));
        int progress_3_num = Integer.parseInt(list.get(7));
        circle_3 = view.findViewById(R.id.census_ticket_circle_3);
        circle_3.setProgress(progress_3_num);
        progress_3 = view.findViewById(R.id.census_ticket_progress_3);
        progress_3.setText(progress_3_num + "%");


        movie_name = view.findViewById(R.id.census_favorite_movie_name);
        movie_name.setText(list.get(8));
        movie_score = view.findViewById(R.id.census_favorite_movie_score);
        movie_score.setText(list.get(9));
        movie_comment = view.findViewById(R.id.census_favorite_movie_comment);
        movie_comment.setText(list.get(10));
    }
}