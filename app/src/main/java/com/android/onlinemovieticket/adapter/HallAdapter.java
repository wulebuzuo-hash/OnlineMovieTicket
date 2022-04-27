package com.android.onlinemovieticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;

import java.util.List;

public class HallAdapter extends RecyclerView.Adapter<HallAdapter.ViewHolder> {
    private List<String> seatList;

    public HallAdapter(List<String> seatList) {
        this.seatList = seatList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView seat_image;
        public ViewHolder(android.view.View itemView) {
            super(itemView);
            seat_image = itemView.findViewById(R.id.item_seat_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_seat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.seat_image.setImageResource(R.drawable.seat_white);
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }
}
