package com.android.onlinemovieticket.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Cinema;
import com.android.onlinemovieticket.db.Hall;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.db.Session;
import com.android.onlinemovieticket.db.Ticket;

import java.text.SimpleDateFormat;
import java.util.List;

public class Soon_ticket_adapter extends RecyclerView.Adapter<Soon_ticket_adapter.ViewHolder> {
    private List<Ticket> showTicketList;
    private List<Session> showSessionList;
    private List<Movie> showMovieList;
    private List<Cinema> showCinemaList;
    private List<Hall> showHallList;
    private boolean flag;

    private onRecyclerItemClickListener listener;

    public Soon_ticket_adapter(List<Ticket> showTicketList, List<Session> showSessionList,
                               List<Movie> showMovieList, List<Cinema> showCinemaList,
                               List<Hall> showHallList, boolean flag) {
        this.showTicketList = showTicketList;
        this.showSessionList = showSessionList;
        this.showMovieList = showMovieList;
        this.showCinemaList = showCinemaList;
        this.showHallList = showHallList;
        this.flag = flag;
    }

    public void setListener(onRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View ticketView;
        CardView ticket_card;
        ImageView ticket_img;
        TextView ticket_price;
        TextView ticket_name;
        TextView ticket_code;
        TextView ticket_seat;
        TextView ticket_time;
        TextView ticket_cinema;
        TextView ticket_hall;

        public ViewHolder(View view) {
            super(view);
            ticketView = view;
            ticket_card = view.findViewById(R.id.item_ticket_card);
            ticket_img = view.findViewById(R.id.item_ticket_image);
            ticket_price = view.findViewById(R.id.item_ticket_price);
            ticket_name = view.findViewById(R.id.item_ticket_name);
            ticket_code = view.findViewById(R.id.item_ticket_code);
            ticket_seat = view.findViewById(R.id.item_ticket_seat);
            ticket_time = view.findViewById(R.id.item_ticket_time);
            ticket_cinema = view.findViewById(R.id.item_ticket_cinema);
            ticket_hall = view.findViewById(R.id.item_ticket_hall);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_ticket, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.ticketView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        });

        holder.ticketView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onLongClick(holder.getAdapterPosition());
                }
                return true;
            }
        });
        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(flag){
            holder.ticket_card.setCardBackgroundColor(R.color.purple_200);
        }else {
            holder.ticket_card.setCardBackgroundColor(R.color.darkOrange);
        }

        Ticket ticket = showTicketList.get(position);
        Session session = showSessionList.get(position);
        Movie movie = showMovieList.get(position);
        String hname = showHallList.get(position).getHname();
        String cname = showCinemaList.get(position).getCname();

        byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes,
                0, imageBytes.length);
        holder.ticket_img.setImageBitmap(decodedImage);
        holder.ticket_name.setText(movie.getMname());
        holder.ticket_code.setText(ticket.getTicket_code());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(session.getShowDate());
        String time = dateFormat2.format(session.getShowTime());
        holder.ticket_time.setText(date + " " + time);
        holder.ticket_cinema.setText(cname);
        holder.ticket_hall.setText(hname);
        holder.ticket_price.setText("￥ " + ticket.getPrice());

        String seat = ticket.getSeat();
        seat = seat.replace(",", "排");
        seat = seat.replace(";", "座 ");
        holder.ticket_seat.setText(seat);
    }

    @Override
    public int getItemCount() {
        return showTicketList.size();
    }
}
