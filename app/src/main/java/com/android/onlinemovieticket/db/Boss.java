package com.android.onlinemovieticket.db;

public class Boss {
    private int bid;
    private String baccount;
    private String bpassword;

    public Boss(int bid, String baccount, String bpassword) {
        this.bid = bid;
        this.baccount = baccount;
        this.bpassword = bpassword;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getBaccount() {
        return baccount;
    }

    public void setBaccount(String baccount) {
        this.baccount = baccount;
    }

    public String getBpassword() {
        return bpassword;
    }

    public void setBpassword(String bpassword) {
        this.bpassword = bpassword;
    }
}
