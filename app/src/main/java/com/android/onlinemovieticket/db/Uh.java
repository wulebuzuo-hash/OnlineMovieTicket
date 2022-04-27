package com.android.onlinemovieticket.db;

import java.util.Date;

public class Uh {
    private int uhId;
    private String uhcontent;
    private int uh_updateId;
    private String uh_table;
    private String uhaccount;
    private Date uhDate;

    public Uh(int uhId, String uhcontent, int uh_updateId,
              String uh_table, String uhaccount, Date uhDate) {
        this.uhId = uhId;
        this.uhcontent = uhcontent;
        this.uh_updateId = uh_updateId;
        this.uh_table = uh_table;
        this.uhaccount = uhaccount;
        this.uhDate = uhDate;
    }

    public Uh(String uhcontent, int uh_updateId,
              String uh_table, String uhaccount, Date uhDate) {
        this.uhcontent = uhcontent;
        this.uh_updateId = uh_updateId;
        this.uh_table = uh_table;
        this.uhaccount = uhaccount;
        this.uhDate = uhDate;
    }

    public String getUh_table() {
        return uh_table;
    }

    public void setUh_table(String uh_table) {
        this.uh_table = uh_table;
    }

    public int getUhId() {
        return uhId;
    }

    public void setUhId(int uhId) {
        this.uhId = uhId;
    }

    public String getUhcontent() {
        return uhcontent;
    }

    public void setUhcontent(String uhcontent) {
        this.uhcontent = uhcontent;
    }

    public int getUh_updateId() {
        return uh_updateId;
    }

    public void setUh_updateId(int uh_updateId) {
        this.uh_updateId = uh_updateId;
    }

    public String getUhaccount() {
        return uhaccount;
    }

    public void setUhaccount(String uhaccount) {
        this.uhaccount = uhaccount;
    }

    public Date getUhDate() {
        return uhDate;
    }

    public void setUhDate(Date uhDate) {
        this.uhDate = uhDate;
    }
}
