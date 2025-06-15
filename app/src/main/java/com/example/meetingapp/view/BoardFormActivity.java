package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.ApiServiceWrapper;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardFormActivity extends BaseActivity {
    ApiService api;
    EditText et_title,et_content;
    Button btn_submit;
    boolean isLoggedIn = ApiServiceWrapper.isLoggedIn(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_form);
        setupToolbar();
        et_title =findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        btn_submit = findViewById(R.id.btn_submit);
        api = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        btn_submit.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Toast.makeText(this, "로그인 후 등록 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = et_title.getText().toString().trim();
            String content = et_content.getText().toString().trim();


            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String username = prefs.getString("username", null);

            Map<String, Object> data = new HashMap<>();
            data.put("subject", title);
            data.put("content", content);
            if (username != null) {
                data.put("author", username);  // 여기서 직접 author를 넣어버림
            }
            api.createQuestion(data).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Map<String, Object> result = response.body();
                        if (result != null && Boolean.TRUE.equals(result.get("success"))) {
                            Toast.makeText(BoardFormActivity.this, "등록 완료", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BoardFormActivity.this, MainActivity.class));
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(BoardFormActivity.this, "등록 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BoardFormActivity.this, "서버 오류", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(BoardFormActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });
        //EDIT MODE
        // onCreate 내부에서
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        if ("edit".equals(mode)) {
            int id = intent.getIntExtra("id", -1);
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            et_title.setText(title);
            et_content.setText(content);

            btn_submit.setText("수정");

            btn_submit.setOnClickListener(v -> {
                String updatedTitle = et_title.getText().toString().trim();
                String updatedContent = et_content.getText().toString().trim();
                SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                String username = prefs.getString("username", null);
                Map<String, Object> data = new HashMap<>();
                data.put("subject", updatedTitle);
                data.put("content", updatedContent);
                if (username != null) {
                    data.put("author", username);  // 여기서 직접 author를 넣어버림
                }
                api.updateQuestion(id, data).enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(BoardFormActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(BoardFormActivity.this, "수정 실패: 권한 없음", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(BoardFormActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

    }
}