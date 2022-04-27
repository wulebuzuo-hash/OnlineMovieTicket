package com.android.onlinemovieticket.db;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Movie implements Serializable {
    private int mid;
    private String mname;
    private String mname_eng;
    private String mscreen;
    private String mtype;
    private String mstory;
    private int mlong;
    private String imgString;
    private Date showdate;
    private Date downdate;
    private String mactor;
    private String mdir;
    private double mpf;
    private int mscall;
    private int mscnum;

    public Movie(int mid, String mname, String mname_eng, String mscreen, String mtype,
                 String mstory, int mlong, String imgString, Date showdate, Date downdate,
                 String mactor, String mdir, double mpf, int mscall, int mscnum) {
        this.mid = mid;
        this.mname = mname;
        this.mname_eng = mname_eng;
        this.mscreen = mscreen;
        this.mtype = mtype;
        this.mstory = mstory;
        this.mlong = mlong;
        this.imgString = imgString;
        this.showdate = showdate;
        this.downdate = downdate;
        this.mactor = mactor;
        this.mdir = mdir;
        this.mpf = mpf;
        this.mscall = mscall;
        this.mscnum = mscnum;
    }

    public Movie(int mid, String mname, String mname_eng, String mscreen, String mtype,
                 String mstory, int mlong, String imgString, Date showdate, Date downdate,
                 String mactor, String mdir) {
        this.mid = mid;
        this.mname = mname;
        this.mname_eng = mname_eng;
        this.mscreen = mscreen;
        this.mtype = mtype;
        this.mstory = mstory;
        this.mlong = mlong;
        this.imgString = imgString;
        this.showdate = showdate;
        this.downdate = downdate;
        this.mactor = mactor;
        this.mdir = mdir;
    }

    public Movie(String mname, String mname_eng, String mscreen, String mtype, String mstory, int mlong,
                 String imgString, Date showdate, Date downdate, String mactor, String mdir,
                 double mpf, int mscall, int mscnum) {
        this.mname = mname;
        this.mname_eng = mname_eng;
        this.mscreen = mscreen;
        this.mtype = mtype;
        this.mstory = mstory;
        this.mlong = mlong;
        this.imgString = imgString;
        this.showdate = showdate;
        this.downdate = downdate;
        this.mactor = mactor;
        this.mdir = mdir;
        this.mpf = mpf;
        this.mscall = mscall;
        this.mscnum = mscnum;
    }

    public Movie(String mname, String mname_eng, String mscreen, String mtype,
                 String mstory, int mlong, String imgString, Date showdate,
                 Date downdate, String mactor, String mdir) {
        this.mname = mname;
        this.mname_eng = mname_eng;
        this.mtype = mtype;
        this.mstory = mstory;
        this.mlong = mlong;
        this.imgString = imgString;
        this.showdate = showdate;
        this.downdate = downdate;
        this.mactor = mactor;
        this.mdir = mdir;
    }

    public String getMscreen() {
        return mscreen;
    }

    public void setMscreen(String mscreen) {
        this.mscreen = mscreen;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMname_eng() {
        return mname_eng;
    }

    public void setMname_eng(String mname_eng) {
        this.mname_eng = mname_eng;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getMstory() {
        return mstory;
    }

    public void setMstory(String mstory) {
        this.mstory = mstory;
    }

    public int getMlong() {
        return mlong;
    }

    public void setMlong(int mlong) {
        this.mlong = mlong;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

    public Date getShowdate() {
        return showdate;
    }

    public void setShowdate(Date showdate) {
        this.showdate = showdate;
    }

    public Date getDowndate() {
        return downdate;
    }

    public void setDowndate(Date downdate) {
        this.downdate = downdate;
    }

    public String getMactor() {
        return mactor;
    }

    public void setMactor(String mactor) {
        this.mactor = mactor;
    }

    public String getMdir() {
        return mdir;
    }

    public void setMdir(String mdir) {
        this.mdir = mdir;
    }

    public double getMpf() {
        return mpf;
    }

    public void setMpf(double mpf) {
        this.mpf = mpf;
    }

    public int getMscall() {
        return mscall;
    }

    public void setMscall(int mscall) {
        this.mscall = mscall;
    }

    public int getMscnum() {
        return mscnum;
    }

    public void setMscnum(int mscnum) {
        this.mscnum = mscnum;
    }
}
