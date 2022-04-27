package com.android.onlinemovieticket.adapter;

import java.util.List;

public interface onRecyclerItemClickListener {
    void onItemClick(int position,boolean isClick);
    void onItemClick(int position);
    void onLongClick(int position);
}
