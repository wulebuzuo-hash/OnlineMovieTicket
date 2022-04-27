package com.android.onlinemovieticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {
    private List<String> seatList;
    private List<Boolean> isClickList;
    private onRecyclerItemClickListener listener;


    public void setListener(onRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public SeatAdapter(List<String> seatList) {

        this.seatList = seatList;
        isClickList = new ArrayList<>(seatList.size());
        for(int i = 0; i < seatList.size(); i++) {
            isClickList.add(false);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView seat_image;
        View view;
        public ViewHolder(android.view.View itemView) {
            super(itemView);
            view = itemView;
            seat_image = (ImageView) itemView.findViewById(R.id.item_seat_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_seat, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.seat_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClickList.get(holder.getAdapterPosition())) {  //已经被点击过
                    holder.seat_image.setImageResource(R.drawable.seat_white);
                    listener.onItemClick(holder.getAdapterPosition(),true);
                    isClickList.set(holder.getAdapterPosition(),false);
                }else { //还没有被点击过
                    listener.onItemClick(holder.getAdapterPosition(),false);
                    isClickList.set(holder.getAdapterPosition(),true);
                    holder.seat_image.setImageResource(R.drawable.seat_green);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (seatList.get(position).equals("1")) {
            holder.seat_image.setImageResource(R.drawable.seat_red);
            holder.seat_image.setEnabled(false);
        } else {
            holder.seat_image.setImageResource(R.drawable.seat_white);
        }

    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }
}
