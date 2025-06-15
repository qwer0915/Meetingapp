package com.example.meetingapp.entity;

public class AnswerItem {
    public Integer id;
    public String author;
    public String create_date;
    public String content;

    public AnswerItem(Integer id,String author, String create_date, String content) {
        this.id=id;
        this.author = author;
        this.create_date = create_date;
        this.content = content;
    }
}

