package com.example.meetingapp.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionResponse {
    @SerializedName("question_list")
    public List<QuestionItem> questionList;
    public int totalCount;
    public PagingMap pagingMap;
}
