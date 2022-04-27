package com.android.onlinemovieticket.db;

import java.io.Serializable;

public class Hall implements Serializable {
    private int hid;
    private int cid;
    private String hname;
    private int row;    //行
    private int column; //列

    public Hall(int hid, int cid, String hname, int row, int column) {
        this.hid = hid;
        this.cid = cid;
        this.hname = hname;
        this.row = row;
        this.column = column;
    }

    public Hall(int cid, String hname, int row, int column) {
        this.cid = cid;
        this.hname = hname;
        this.row = row;
        this.column = column;
    }

    public Hall(int cid, String hname) {
        this.cid = cid;
        this.hname = hname;
    }

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
