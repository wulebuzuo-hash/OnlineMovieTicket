package com.android.onlinemovieticket.db;

import java.io.Serializable;

public class Admin implements Serializable {
    private int aid;
    private String aaccount;
    private String apassword;
    private int cid;
    private String aname;
    private int asex;
    private String aidCard;
    private String acall;
    private String amail;


    public Admin(int aid, String aaccount, String apassword, int cid, String aname,
                 int asex, String aidCard, String acall, String amail) {
        this.aid = aid;
        this.aaccount = aaccount;
        this.apassword = apassword;
        this.cid = cid;
        this.aname = aname;
        this.asex = asex;
        this.aidCard = aidCard;
        this.acall = acall;
        this.amail = amail;
    }

    public Admin(String aaccount, String apassword, int cid, String aname, int asex,
                 String aidCard, String acall, String amail) {
        this.aaccount = aaccount;
        this.apassword = apassword;
        this.cid = cid;
        this.aname = aname;
        this.asex = asex;
        this.aidCard = aidCard;
        this.acall = acall;
        this.amail = amail;
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

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public int getAsex() {
        return asex;
    }

    public void setAsex(int asex) {
        this.asex = asex;
    }

    public String getAidCard() {
        return aidCard;
    }

    public void setAidCard(String aidCard) {
        this.aidCard = aidCard;
    }

    public String getAcall() {
        return acall;
    }

    public void setAcall(String acall) {
        this.acall = acall;
    }

    public String getAmail() {
        return amail;
    }

    public void setAmail(String amail) {
        this.amail = amail;
    }
}
