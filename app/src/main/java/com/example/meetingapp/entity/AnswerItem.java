package com.example.meetingapp.entity;

public class AnswerItem {

    public Integer id;            // 답변 고유 ID (answerId)
    public String author;
    public int question_id;       // 소속 질문 ID
    public String create_date;
    public String content;

    public AnswerItem(Integer id, String author, int question_id, String create_date, String content) {
        this.id = id;
        this.author = author;
        this.question_id = question_id;
        this.create_date = create_date;
        this.content = content;
    }
}

