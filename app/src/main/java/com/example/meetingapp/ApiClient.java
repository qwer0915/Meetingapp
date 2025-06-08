package com.example.meetingapp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit =null;

    public static Retrofit getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new SessionCookieJar()) //Session 유지용
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8090/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
