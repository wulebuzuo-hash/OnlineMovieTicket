package com.android.onlinemovieticket.db;

import java.io.Serializable;

public class Ticket implements Serializable {
    private int tid;
    private String ticket_code;
    private String uaccount;
    private int sid;
    private int seat_Row;
    private int seat_Column;
    private String seat;
    private double price;
    private int isGrade;

    public Ticket(int tid, String ticket_code, String uaccount, int sid, String seat, double price,
                  int isGrade) {
        this.tid = tid;
        this.ticket_code = ticket_code;
        this.uaccount = uaccount;
        this.sid = sid;
        this.seat = seat;
        this.price = price;
        this.isGrade = isGrade;
    }

    public Ticket(String ticket_code, String uaccount, int sid,
                  String seat, double price, int isGrade) {
        this.ticket_code = ticket_code;
        this.uaccount = uaccount;
        this.sid = sid;
        this.seat = seat;
        this.price = price;
        this.isGrade = isGrade;
    }

    public Ticket(String uaccount, int sid, int seat_Row, int seat_Column) {
        this.uaccount = uaccount;
        this.sid = sid;
        this.seat_Row = seat_Row;
        this.seat_Column = seat_Column;
    }

    public int getIsGrade() {
        return isGrade;
    }

    public void setIsGrade(int isGrade) {
        this.isGrade = isGrade;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTicket_code() {
        return ticket_code;
    }

    public void setTicket_code(String ticket_code) {
        this.ticket_code = ticket_code;
    }

    public String getUaccount() {
        return uaccount;
    }

    public void setUaccount(String uaccount) {
        this.uaccount = uaccount;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public int getSeat_Row() {
        return seat_Row;
    }

    public void setSeat_Row(int seat_Row) {
        this.seat_Row = seat_Row;
    }

    public int getSeat_Column() {
        return seat_Column;
    }

    public void setSeat_Column(int seat_Column) {
        this.seat_Column = seat_Column;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
