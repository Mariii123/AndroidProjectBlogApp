package com.example.marishwaran.project01;

public class Comment {
    String title;
    String comment;
    public Comment(){

    }
    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public Comment(String title, String comment) {

        this.title = title;
        this.comment = comment;
    }


}
