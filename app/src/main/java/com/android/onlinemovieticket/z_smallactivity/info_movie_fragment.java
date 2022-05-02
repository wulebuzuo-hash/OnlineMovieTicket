package com.android.onlinemovieticket.z_smallactivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.R;

public class info_movie_fragment extends Fragment {

    private TextView movie_sc;
    private RatingBar movie_rating;
    private TextView movie_scnum;
    private TextView movie_pf;
    private TextView movie_story;
    private TextView movie_director;
    private TextView movie_actor;
    private Button movie_buy;

    private String account;
    private String type;
    private int mid;
    private double ticket_price;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_movie_fragment, container, false);
        movie_sc = view.findViewById(R.id.movie_fragment_sc);
        movie_rating = view.findViewById(R.id.movie_fragment_rating);
        movie_scnum = view.findViewById(R.id.movie_fragment_scnum);
        movie_pf = view.findViewById(R.id.movie_fragment_pf);
        movie_story = view.findViewById(R.id.movie_fragment_story);
        movie_director = view.findViewById(R.id.movie_fragment_director);
        movie_actor = view.findViewById(R.id.movie_fragment_actor);
        movie_buy = view.findViewById(R.id.movie_fragment_buy);

        movie_sc.setText(getArguments().getString("sc"));
        movie_rating.setRating(getArguments().getFloat("rating"));
        movie_scnum.setText(getArguments().getString("scnum"));
        movie_pf.setText(getArguments().getString("pf"));
        movie_story.setText(getArguments().getString("story"));
        movie_director.setText(getArguments().getString("director"));
        movie_actor.setText(getArguments().getString("actor"));

        account = getArguments().getString("account");
        type = getArguments().getString("type");
        mid = getArguments().getInt("mid");
        ticket_price = getArguments().getDouble("ticket_price");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        movie_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CinemaActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("mid", mid);
                intent.putExtra("ticket_price", ticket_price);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}