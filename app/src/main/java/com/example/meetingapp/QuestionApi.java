package com.example.meetingapp;

import com.example.meetingapp.entity.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuestionApi {
    @GET("api/question/list")
    Call<List<Question>> getQuestionList();

}
