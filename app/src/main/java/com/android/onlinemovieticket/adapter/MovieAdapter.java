package com.android.onlinemovieticket.adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.CinemaActivity;
import com.android.onlinemovieticket.MovieActivity;
import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Movie;
import com.android.onlinemovieticket.repository.MovieRepository;
import com.android.onlinemovieticket.z_smallactivity.Info_Movie;
import com.android.onlinemovieticket.z_smallactivity.List_Comment;
import com.android.onlinemovieticket.z_smallactivity.List_Session;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private List<Movie> movieList;
    private String account;
    private String type;
    private double ticket_price;
    private onRecyclerItemClickListener listener;

    private int selposition = 0;    //记录选中的位置
    private int temp = -1;  //记录上一次选中的位置

    public void setListener(onRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View movieView;
        TextView movieName;
        TextView movieRating;
        TextView movieReleaseDate;
        ImageView movieImage;
        TextView movieType;
        Button movieBook;

        public ViewHolder(View view) {
            super(view);
            movieView = view;
            movieName = (TextView) view.findViewById(R.id.item_movie_name);
            movieRating = (TextView) view.findViewById(R.id.item_movie_rating);
            movieReleaseDate = (TextView) view.findViewById(R.id.item_movie_date);
            movieImage = (ImageView) view.findViewById(R.id.item_movie_image);
            movieType = (TextView) view.findViewById(R.id.item_movie_type1);
            movieBook = (Button) view.findViewById(R.id.item_movie_book);
        }
    }

    public MovieAdapter(List<Movie> movieList, String account, String type, double ticket_price) {
        this.movieList = movieList;
        this.account = account;
        this.type = type;
        this.ticket_price = ticket_price;
    }

    public MovieAdapter(List<Movie> movieList, String type) {
        this.movieList = movieList;
        this.type = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_movie, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Movie movie = movieList.get(position);
        holder.movieName.setText(movie.getMname());

        Date currentDate = getNowDate();
        if (getDateDiff(currentDate, movie.getShowdate()) <= 0) {
            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            holder.movieReleaseDate.setVisibility(View.GONE);
            holder.movieRating.setVisibility(View.VISIBLE);
            holder.movieRating.setText(String.format(
                    "%.1f", (double) movie.getMscall() / movie.getMscnum()) + "分");
        } else {
            holder.movieReleaseDate.setVisibility(View.VISIBLE);
            holder.movieRating.setVisibility(View.GONE);
            DateFormat sdf = new SimpleDateFormat("MM-dd");
            holder.movieReleaseDate.setText(sdf.format(movie.getShowdate()) + "上映");
        }

        byte[] imageBytes = Base64.decode(movie.getImgString(), Base64.DEFAULT);
        Glide.with(holder.movieImage.getContext()).load(BitmapFactory.decodeByteArray(
                imageBytes, 0, imageBytes.length)).into(holder.movieImage);

        holder.movieType.setText(movie.getMscreen());

        if (type.equals("用户") && account != null &&
                getDateDiff(currentDate, movie.getShowdate()) <= 7) {
            holder.movieBook.setVisibility(View.VISIBLE);
        } else {
            holder.movieBook.setVisibility(View.GONE);
            if (type.equals("管理员")) {
                holder.movieView.setBackground(holder.movieView.getContext().getResources().
                        getDrawable(R.drawable.bg_item_movie));
                holder.movieView.setSelected(holder.getLayoutPosition() == selposition);
            }
        }

        holder.movieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (account != null) {    //表明是从MovieActivity跳转过来的
                    Intent intent = null;
                    if (type.equals("用户")) {
                        intent = new Intent(v.getContext(), List_Comment.class);
                        intent.putExtra("ticket_price", ticket_price);
                    } else if (type.equals("BOSS")) {
                        intent = new Intent(v.getContext(), Info_Movie.class);
                    }
                    intent.putExtra("mid", movie.getMid());
                    intent.putExtra("account", account);
                    intent.putExtra("type", type);
                    v.getContext().startActivity(intent);

                } else { //表明是从List_Session跳转过来的
                    holder.movieView.setSelected(true);
                    temp = selposition;
                    selposition = holder.getLayoutPosition();
                    notifyItemChanged(temp);
                    listener.onItemClick(position);
                }
            }
        });

        holder.movieView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (type.equals("BOSS")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("删除电影" + movie.getMname());
                    builder.setMessage("确定删除吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread() {
                                @Override
                                public void run() {
                                    MovieRepository movieRepository = new MovieRepository();
                                    boolean flag = movieRepository.deleteMovie(movie.getMid());
                                    if (flag) {
                                        Toast.makeText(holder.movieView.getContext(),
                                                "删除成功", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(v.getContext(),
                                                MovieActivity.class);
                                        intent.putExtra("account", account);
                                        intent.putExtra("type", type);
                                        v.getContext().startActivity(intent);
                                    } else {
                                        Toast.makeText(holder.movieView.getContext(),
                                                "删除失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }.start();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                return true;
            }
        });

        holder.movieBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CinemaActivity.class);
                intent.putExtra("account", account);
                intent.putExtra("type", type);
                intent.putExtra("mid", movieList.get(position).getMid());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    //获取现在时间
    public Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);

        return currentTime_2;
    }

    //计算日期差
    public int getDateDiff(Date startDate, Date endDate) {
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
        return days;
    }
}
