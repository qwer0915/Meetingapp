package com.example.meetingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingapp.util.SessionManager;
import com.example.meetingapp.view.LoginActivity;
import com.example.meetingapp.view.MainActivity;
import com.example.meetingapp.view.RegisterActivity;

public class BaseActivity extends AppCompatActivity {
    private boolean isLoggedIn = false;
    protected Toolbar toolbar;
    protected TextView titleView;
    protected Button btnLogin, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SessionManager.init(getApplicationContext());
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (toolbar != null) {
            updateAuthButtons(); // 항상 로그인 상태에 따라 갱신
        }
    }
    protected void setupToolbar() {
        toolbar     =       findViewById(R.id.main_toolbar);
        titleView   =       findViewById(R.id.toolbar_title);
        btnLogin    =       findViewById(R.id.btn_login);
        btnRegister =       findViewById(R.id.btn_register);

        titleView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        // 회원가입
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        updateAuthButtons();
    }
    protected void updateAuthButtons() {
        boolean isLoggedIn = ApiServiceWrapper.isLoggedIn(this);
        if (isLoggedIn) {
            btnLogin.setText("로그아웃");
            btnRegister.setVisibility(View.GONE);

            btnLogin.setOnClickListener(v -> {
                ApiServiceWrapper.logout(this, () -> {
                    updateAuthButtons();
                    Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                });
            });
        } else {
            btnLogin.setText("로그인");
            btnRegister.setVisibility(View.VISIBLE);

            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            });
        }
    }
}