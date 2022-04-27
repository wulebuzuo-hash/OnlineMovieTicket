package com.android.onlinemovieticket.db;

import java.io.Serializable;

public class Cinema implements Serializable {
    private int cid;
    private String cname;
    private String cposition;
    private String ccall;

    public Cinema(int cid, String cname, String cposition, String ccall) {
        this.cid = cid;
        this.cname = cname;
        this.cposition = cposition;
        this.ccall = ccall;
    }

    public Cinema(String cname, String cposition, String ccall) {
        this.cname = cname;
        this.cposition = cposition;
        this.ccall = ccall;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCposition() {
        return cposition;
    }

    public void setCposition(String cposition) {
        this.cposition = cposition;
    }

    public String getCcall() {
        return ccall;
    }

    public void setCcall(String ccall) {
        this.ccall = ccall;
    }
}
