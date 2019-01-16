package com.example.marishwaran.project01;

import java.util.Date;

public class FeedbackList {
    String fname, fimg, fdesc;
    Date ftimestamp;

    public FeedbackList() {

    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFimg() {
        return fimg;
    }

    public void setFimg(String fimg) {
        this.fimg = fimg;
    }

    public String getFdesc() {
        return fdesc;
    }

    public void setFdesc(String fdesc) {
        this.fdesc = fdesc;
    }

    public Date getFtimestamp() {
        return ftimestamp;
    }

    public void setFtimestamp(Date ftimestamp) {
        this.ftimestamp = ftimestamp;
    }
    public FeedbackList(String fname, String fimg, String fdesc, Date ftimestamp) {
        this.fname = fname;
        this.fimg = fimg;
        this.fdesc = fdesc;
        this.ftimestamp = ftimestamp;
    }
}
