package com.example.meetingapp;

import com.example.meetingapp.entity.QuestionResponse;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("user/login")
    Call<Map<String,Object>> login(@Body Map<String,Object> loginData);
    @POST("user/logout")
    Call<Void> logout();
    @POST("user/join")
    Call<Map<String,Object>> register(@Body Map<String,Object> registerData);

    @GET("question/{id}")
    Call<Map<String, Object>> getQuestionDetail(@Path("id") int id);

    @GET("question/list")
    Call<QuestionResponse> getQuestions(
            @Query("searchSubjectName") String keyword,
            @Query("pageNum") int page
    );
    @POST("question/create")
    Call<Map<String, Object>> createQuestion(@Body Map<String, Object> param);
    @POST("question/update/{id}")
    Call<Map<String, Object>> updateQuestion(@Path("id") int id, @Body Map<String, Object> body);

    @POST("question/delete/{id}")
    Call<Map<String, Object>> deleteQuestion(@Path("id") int id, @Body Map<String, Object> body);
    @POST("answer/insert/{questionId}")
    Call<Map<String, Object>> createAnswer(@Path("questionId") int questionId, @Body Map<String, Object> content);
    @POST("answer/update")
    Call<Map<String, Object>> updateAnswer(@Body Map<String, Object> body);
    @POST("answer/delete")
    Call<Map<String, Object>> deleteAnswer(@Body Map<String, Object> body);



}
