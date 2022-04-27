package com.android.onlinemovieticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Ticket;

import java.util.List;

public class ChooseSeatAdapter extends RecyclerView.Adapter<ChooseSeatAdapter.ViewHolder> {
    private List<Ticket> ticketList;;

    public ChooseSeatAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView seat_position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seat_position = itemView.findViewById(R.id.item_seat_choose_position);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_seat_choose, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        if(ticket != null) {
            String seat = (ticket.getSeat_Row() + 1) + "排" + (ticket.getSeat_Column() + 1) + "座";
            holder.seat_position.setText(seat);
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }
}
