package com.android.onlinemovieticket.db;


import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Session implements Serializable {
    private int sid;
    private int cid;
    private int hid;
    private int mid;
    private Date showDate;
    private Date showTime;
    private Date endTime;
    private Double price;
    private String state;

    public Session(int sid, int cid, int hid, int mid, Date showDate,
                   Date showTime, Date endTime, Double price, String state) {
        this.sid = sid;
        this.cid = cid;
        this.hid = hid;
        this.mid = mid;
        this.showDate = showDate;
        this.showTime = showTime;
        this.endTime = endTime;
        this.price = price;
        this.state = state;
    }

    public Session(int cid, int hid, int mid, Date showDate,
                   Date showTime, Date endTime, Double price, String state) {
        this.cid = cid;
        this.hid = hid;
        this.mid = mid;
        this.showDate = showDate;
        this.showTime = showTime;
        this.endTime = endTime;
        this.price = price;
        this.state = state;
    }

    public Session(int hid, int mid, Date showDate, Date showTime) {
        this.hid = hid;
        this.mid = mid;
        this.showDate = showDate;
        this.showTime = showTime;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
