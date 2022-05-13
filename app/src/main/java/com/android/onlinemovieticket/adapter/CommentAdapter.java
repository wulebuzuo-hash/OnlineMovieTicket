package com.android.onlinemovieticket.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.onlinemovieticket.R;
import com.android.onlinemovieticket.db.Comment;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mCommentList;
    private boolean[] isGood;
    private onRecyclerItemClickListener listener;

    public void setListener(onRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public CommentAdapter(String account, List<Comment> mCommentList) {
        this.mCommentList = mCommentList;
        isGood = new boolean[mCommentList.size()];
        for (int i = 0; i < mCommentList.size(); i++) {
            if (mCommentList.get(i).getGood_user_id().contains(account)) {
                isGood[i] = true;
            } else {
                isGood[i] = false;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_image;
        TextView user_account;
        RatingBar ratingBar;
        TextView comment_text;
        TextView comment_time;
        ImageView good_image;
        TextView good_num;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            user_image = (ImageView) itemView.findViewById(R.id.item_comment_user_image);
            user_account = (TextView) itemView.findViewById(R.id.item_comment_user_account);
            ratingBar = (RatingBar) itemView.findViewById(R.id.item_comment_rating);
            comment_text = (TextView) itemView.findViewById(R.id.item_comment_text);
            comment_time = (TextView) itemView.findViewById(R.id.item_comment_time);
            good_image = (ImageView) itemView.findViewById(R.id.item_comment_good_img);
            good_num = (TextView) itemView.findViewById(R.id.item_comment_good_num);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(
                mContext).inflate(R.layout.item_comment, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getAdapterPosition();
                if (listener != null) {
                    listener.onLongClick(position);
                }
                return true;
            }
        });

        holder.good_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGood[holder.getAdapterPosition()]) {
                    holder.good_image.setImageResource(R.drawable.ic_comment_good_0);
                    holder.good_num.setText(String.valueOf(Integer.parseInt(
                            holder.good_num.getText().toString()) - 1));
                    isGood[holder.getAdapterPosition()] = false;
                    listener.onItemClick(holder.getAdapterPosition(), false);
                } else {
                    holder.good_image.setImageResource(R.drawable.ic_comment_good_1);
                    holder.good_num.setText(String.valueOf(Integer.parseInt(
                            holder.good_num.getText().toString()) + 1));
                    isGood[holder.getAdapterPosition()] = true;
                    listener.onItemClick(holder.getAdapterPosition(), true);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mCommentList.get(position);

        byte[] imageBytes = Base64.decode(comment.getUser_image(), Base64.DEFAULT);
        Glide.with(mContext).load(BitmapFactory.
                decodeByteArray(imageBytes, 0, imageBytes.length)).into(holder.user_image);

        int score = comment.getSc();
        if (score == -1) {
            holder.ratingBar.setVisibility(View.INVISIBLE);
        } else {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating((float) score / 2);
        }
        holder.user_account.setText(comment.getUaccount());
        holder.comment_text.setText(comment.getComment_text());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        holder.comment_time.setText(dateFormat.format(comment.getComment_time()));
        holder.good_num.setText(String.valueOf(comment.getGood_num()));
        if (isGood[position]) {
            holder.good_image.setImageResource(R.drawable.ic_comment_good_1);
        } else {
            holder.good_image.setImageResource(R.drawable.ic_comment_good_0);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

}
