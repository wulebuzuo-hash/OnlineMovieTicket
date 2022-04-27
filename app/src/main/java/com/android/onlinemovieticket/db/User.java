package com.android.onlinemovieticket.db;

public class User {
    private int uid;
    private String uaccount;
    private String upassword;
    private String uquestion1;
    private String uanswer1;
    private String uquestion2;
    private String answer2;
    private String imageString;

    public User(int uid, String uaccount, String upassword, String uquestion1, String uanswer1,
                String uquestion2, String answer2, String imageString) {
        this.uid = uid;
        this.uaccount = uaccount;
        this.upassword = upassword;
        this.uquestion1 = uquestion1;
        this.uanswer1 = uanswer1;
        this.uquestion2 = uquestion2;
        this.answer2 = answer2;
        this.imageString = imageString;
    }

    public User(int uid, String uaccount, String upassword,
                String uquestion1, String uanswer1, String uquestion2, String answer2) {
        this.uid = uid;
        this.uaccount = uaccount;
        this.upassword = upassword;
        this.uquestion1 = uquestion1;
        this.uanswer1 = uanswer1;
        this.uquestion2 = uquestion2;
        this.answer2 = answer2;
    }

    public User(String uaccount, String upassword,
                String uquestion1, String uanswer1, String uquestion2, String answer2) {
        this.uaccount = uaccount;
        this.upassword = upassword;
        this.uquestion1 = uquestion1;
        this.uanswer1 = uanswer1;
        this.uquestion2 = uquestion2;
        this.answer2 = answer2;
    }

    public User(String uaccount, String uquestion1,
                String uanswer1, String uquestion2, String answer2) {
        this.uaccount = uaccount;
        this.uquestion1 = uquestion1;
        this.uanswer1 = uanswer1;
        this.uquestion2 = uquestion2;
        this.answer2 = answer2;
    }



    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUaccount() {
        return uaccount;
    }

    public void setUaccount(String uaccount) {
        this.uaccount = uaccount;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public String getUquestion1() {
        return uquestion1;
    }

    public void setUquestion1(String uquestion1) {
        this.uquestion1 = uquestion1;
    }

    public String getUanswer1() {
        return uanswer1;
    }

    public void setUanswer1(String uanswer1) {
        this.uanswer1 = uanswer1;
    }

    public String getUquestion2() {
        return uquestion2;
    }

    public void setUquestion2(String uquestion2) {
        this.uquestion2 = uquestion2;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
