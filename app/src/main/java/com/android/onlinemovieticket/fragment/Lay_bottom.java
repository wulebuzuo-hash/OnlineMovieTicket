package com.android.onlinemovieticket.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.LoginActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.My_User;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.z_smallactivity.List_Admin;
import com.android.onlinemovieticket.z_smallactivity.List_Hall;
import com.android.onlinemovieticket.z_smallactivity.List_Session;
import com.android.onlinemovieticket.z_smallactivity.List_Uh;
import com.android.onlinemovieticket.z_smallactivity.List_UserHistory;

public class Lay_bottom extends Fragment {

    RadioGroup radioGroup;
    RadioButton bottom_1;
    RadioButton bottom_2;
    RadioButton bottom_3;
    RadioButton bottom_4;

    private String account;
    private String type;
    private int cid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lay_bottom, container, false);

        account = getArguments().getString("account");
        type = getArguments().getString("type");
        cid = getArguments().getInt("cid");

        radioGroup = view.findViewById(R.id.bottom_radio_group);
        bottom_1 = view.findViewById(R.id.bottom_choose_movie);
        bottom_2 = view.findViewById(R.id.bottom_choose_cinema);
        bottom_3 = view.findViewById(R.id.bottom_choose_my);
        bottom_4 = view.findViewById(R.id.bottom_choose_user);
        bottom_4.setVisibility(View.GONE);
        setBounds(R.drawable.pc_movie, bottom_1);
        setBounds(R.drawable.pc_cinema, bottom_2);
        setBounds(R.drawable.my, bottom_3);

        if (type.equals("管理员")) {
            setBounds(R.drawable.pc_hall, bottom_2);
            setBounds(R.drawable.pc_history, bottom_3);
            setBounds(R.drawable.my, bottom_4);
            bottom_2.setText("放映厅");
            bottom_3.setText("管理历史");
            bottom_4.setVisibility(View.VISIBLE);
        } else if (type.equals("BOSS")) {
            bottom_3.setText("管理员");

        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bottom_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                if (type.equals("管理员")) {
                    intent = new Intent(getActivity(), List_Session.class);
                    intent.putExtra("cid", cid);
                } else {
                    intent = new Intent(getActivity(), MovieActivity.class);
                }
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                getActivity().finish();
            }
        });

        bottom_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (type.equals("管理员")) {
                    intent = new Intent(getActivity(), List_Hall.class);
                    intent.putExtra("cid", cid);
                } else {
                    intent = new Intent(getActivity(), CinemaActivity.class);
                }
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                getActivity().finish();
            }
        });

        bottom_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                if (type.equals("管理员")) {
                    intent = new Intent(getActivity(), List_Uh.class);
                    intent.putExtra("cid", cid);
                } else if (type.equals("BOSS")) {
                    intent = new Intent(getActivity(), List_Admin.class);
                } else {
                    intent = new Intent(getActivity(), My_User.class);
                }
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                getActivity().finish();
            }
        });

        bottom_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), List_UserHistory.class);
                intent.putExtra("cid", cid);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    /**
     * 设置底部导航栏大小
     * @param drawableId  drawableLeft  drawableTop drawableBottom 所用的选择器 通过R.drawable.xx 获得
     * @param radioButton 需要限定图片大小的radioButton
     */
    private void setBounds(int drawableId, RadioButton radioButton) {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(drawableId);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形  (这里的长和宽写死了 自己可以可以修改成 形参传入)
        drawable_news.setBounds(0, 0, 120, 120);
        //设置图片在文字的哪个方向
        radioButton.setCompoundDrawables(null, drawable_news, null, null);
    }
}