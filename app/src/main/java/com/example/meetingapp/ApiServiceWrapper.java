package com.example.meetingapp;

import android.content.Context;
import android.util.Log;

import com.example.meetingapp.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ApiServiceWrapper {
    private static final ApiService api = ApiClient.getClient().create(ApiService.class);

    public static boolean isLoggedIn(Context context) {
        SessionManager.init(context);
        return SessionManager.isLoggedIn();
    }

    public static void logout(Context context, Runnable onSuccess) {
        api.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                SessionManager.logout();
                onSuccess.run();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 실패하더라도 클라이언트 상태 초기화
                SessionManager.logout();
                onSuccess.run();
            }
        });
    }
}
