package com.example.meetingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {
    EditText etRegisterId,etRegisterPassword,etConfirmPassword,etRegisterEmail;
    Button btnRegister;
    TextView tvGoToLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupToolbar();
        etRegisterId = findViewById(R.id.etRegisterId);
        etRegisterPassword =findViewById(R.id.etRegisterPassword);
        etConfirmPassword =findViewById(R.id.etConfirmPassword);
        etRegisterEmail =findViewById(R.id.etRegisterEmail);
        btnRegister =findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvGoToLogin=findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
    private void handleRegister(){
        String id = etRegisterId.getText().toString().trim();
        String pw = etRegisterPassword.getText().toString().trim();
        String confirmPw = etConfirmPassword.getText().toString().trim();
        String email =etRegisterEmail.getText().toString().trim();

        if (id.isEmpty() || pw.isEmpty() || confirmPw.isEmpty() || email.isEmpty() ) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pw.equals(confirmPw)) {
            Toast.makeText(this, "비밀번호를 다시 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> registerData = new HashMap<>();
        registerData.put("username", id);
        registerData.put("password", pw);
        registerData.put("email", email);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.register(registerData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                    finish(); // 로그인 화면으로 돌아가기
                } else {
                    Toast.makeText(RegisterActivity.this, "회원가입 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}