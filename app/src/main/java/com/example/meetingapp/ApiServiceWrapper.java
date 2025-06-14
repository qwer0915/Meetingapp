package com.example.meetingapp;

import android.content.Context;
import android.util.Log;

import com.example.meetingapp.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ApiServiceWrapper {
    public static boolean isLoggedIn(Context context) {
        SessionManager.init(context);
        return SessionManager.isLoggedIn();
    }

    public static void logout(Context context, Runnable onSuccess) {
        ApiService api = ApiClient.getClient(context).create(ApiService.class);

        api.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                SessionManager.logout();
                onSuccess.run();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                SessionManager.logout();
                onSuccess.run();
            }
        });
    }
}
