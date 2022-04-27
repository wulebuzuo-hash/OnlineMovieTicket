package com.android.onlinemovieticket.z_smallactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;

public class Info_User extends Fragment {

    private RadioButton updatePass;
    private DrawerLayout drawerLayout;
    private RadioButton updateInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_user, container, false);

        updatePass = (RadioButton) view.findViewById(R.id.info_us_updatePass);
        drawerLayout = (DrawerLayout)view.findViewById(R.id.info_user_lay);

        updateInfo = (RadioButton) view.findViewById(R.id.info_us_updatequestion);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), User_updateInfo.class);
                startActivity(intent);
            }
        });
    }
}