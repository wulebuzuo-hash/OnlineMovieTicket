package com.android.onlinemovieticket.db;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {
    private int comment_id;
    private int mid;
    private String user_image;
    private String uaccount;
    private int sc;
    private String comment_text;
    private Date comment_time;
    private int good_num;
    private String good_user_id;
    private int other_comment_id;

    public Comment(int comment_id, int mid, String user_image, String uaccount, int sc,
                   String comment_text, Date comment_time, int good_num, String good_user_id,
                   int other_comment_id) {
        this.comment_id = comment_id;
        this.mid = mid;
        this.user_image = user_image;
        this.uaccount = uaccount;
        this.sc = sc;
        this.comment_text = comment_text;
        this.comment_time = comment_time;
        this.good_num = good_num;
        this.good_user_id = good_user_id;
        this.other_comment_id = other_comment_id;
    }

    public Comment(int mid, String user_image, String uaccount, int sc,
                   String comment_text, Date comment_time) {
        this.mid = mid;
        this.user_image = user_image;
        this.uaccount = uaccount;
        this.sc = sc;
        this.comment_text = comment_text;
        this.comment_time = comment_time;
    }

    public Comment(int mid, String user_image, String uaccount, int sc, String comment_text,
                   Date comment_time, int good_num, String good_user_id, int other_comment_id) {
        this.mid = mid;
        this.user_image = user_image;
        this.uaccount = uaccount;
        this.sc = sc;
        this.comment_text = comment_text;
        this.comment_time = comment_time;
        this.good_num = good_num;
        this.good_user_id = good_user_id;
        this.other_comment_id = other_comment_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUaccount() {
        return uaccount;
    }

    public void setUaccount(String uaccount) {
        this.uaccount = uaccount;
    }

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public Date getComment_time() {
        return comment_time;
    }

    public void setComment_time(Date comment_time) {
        this.comment_time = comment_time;
    }

    public int getGood_num() {
        return good_num;
    }

    public void setGood_num(int good_num) {
        this.good_num = good_num;
    }

    public String getGood_user_id() {
        return good_user_id;
    }

    public void setGood_user_id(String good_user_id) {
        this.good_user_id = good_user_id;
    }

    public int getOther_comment_id() {
        return other_comment_id;
    }

    public void setOther_comment_id(int other_comment_id) {
        this.other_comment_id = other_comment_id;
    }
}
