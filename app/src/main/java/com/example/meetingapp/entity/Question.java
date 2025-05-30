package com.example.meetingapp.entity;

import java.util.List;

public class Question {
    private Long id;
    private String subject;
    private SiteUser author;
    private String createDate;
    private List<Answer> answerList;

}
