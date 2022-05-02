package com.android.onlinemovieticket.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.z_smallactivity.List_Session;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {
    private List<String> dateList;
    private int pos;    //选择的位置
    private int temp = -1;  //记录上一次选择的位置

    private onRecyclerItemClickListener listener;

    public void setListener(onRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public SessionAdapter(List<String> dateList) {
        this.dateList = dateList;
        pos = 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView dateText;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            dateText = (TextView) itemView.findViewById(R.id.item_sessionDate_item);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_session_date, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = dateList.get(position);
        holder.dateText.setText(date);
        holder.view.setSelected(holder.getLayoutPosition() == pos);

        if (listener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.view.setSelected(true);
                    temp = pos;
                    pos = holder.getLayoutPosition();
                    notifyItemChanged(temp);
                    listener.onItemClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

}