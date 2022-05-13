package com.android.onlinemovieticket.db;

import java.io.Serializable;

public class Admin implements Serializable {
    private int aid;
    private String aaccount;
    private String apassword;
    private int cid;

    public Admin(int aid, String aaccount, String apassword, int cid) {
        this.aid = aid;
        this.aaccount = aaccount;
        this.apassword = apassword;
        this.cid = cid;
    }

    public Admin(String aaccount, String apassword, int cid) {
        this.aaccount = aaccount;
        this.apassword = apassword;
        this.cid = cid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getAaccount() {
        return aaccount;
    }

    public void setAaccount(String aaccount) {
        this.aaccount = aaccount;
    }

    public String getApassword() {
        return apassword;
    }

    public void setApassword(String apassword) {
        this.apassword = apassword;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
