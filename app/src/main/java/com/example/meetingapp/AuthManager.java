package com.example.meetingapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    public static void login(Context context) {
        context.getSharedPreferences("auth", MODE_PRIVATE)
                .edit().putBoolean("isLoggedIn", true).apply();
    }

    public static void logout(Context context) {
        context.getSharedPreferences("auth", MODE_PRIVATE)
                .edit().putBoolean("isLoggedIn", false).apply();
    }
}
