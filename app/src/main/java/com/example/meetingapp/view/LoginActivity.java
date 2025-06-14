package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;
import com.example.meetingapp.util.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity  extends BaseActivity {
    Map<String,Object> loginData =new HashMap<String,Object>();

    EditText etId,etPassword;
    Button btnLogin;
    TextView tvGoToRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupToolbar();
        etId = findViewById(R.id.etID);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this::onClick);
        tvGoToRegister=findViewById(R.id.tvGoToRegister);
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void onLoginSuccess() {
        SessionManager.setLoggedIn(true);
        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
        finish(); // 뒤로 돌아가면 BaseActivity에서 버튼이 바뀜
    }

    private void onClick(View v) {
        String id = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (id.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        loginData.clear();
        loginData.put("username", id);
        loginData.put("password", password);
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        apiService.login(loginData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object result = response.body().get("success");
                    if (Boolean.TRUE.equals(result)) {
                        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                        prefs.edit().putString("username",id).apply();
                        onLoginSuccess();
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "서버 응답 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}