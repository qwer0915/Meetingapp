package com.example.meetingapp.view;


import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailActivity extends BaseActivity {
    private TextView tvTitle, tvContent, tvAuthor, tvDate, tvRecommendCount, tvAnswerCount;
    private EditText etAnswer;
    private Button btnRecommend, btnModify, btnDelete, btnSubmitAnswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);
        setupToolbar();

        initViews();
        loadQuestionDetail(1);

    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvAuthor = findViewById(R.id.tv_author);
        tvDate = findViewById(R.id.tv_date);
        tvRecommendCount = findViewById(R.id.tv_recommend_count);
        tvAnswerCount = findViewById(R.id.tv_answer_count);
        etAnswer = findViewById(R.id.et_answer);
        btnRecommend = findViewById(R.id.btn_recommend);
        btnModify = findViewById(R.id.btn_modify);
        btnDelete = findViewById(R.id.btn_delete);
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
    }

    private void loadQuestionDetail(int id) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getQuestionDetail(id).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Map<String, Object> root = response.body();
                    Map<String, Object> questionDetail = (Map<String, Object>) root.get("questionDetail");

                    if (questionDetail != null) {
                        tvTitle.setText((String) questionDetail.get("subject"));
                        tvContent.setText((String) questionDetail.get("content"));
                        tvAuthor.setText((String) questionDetail.get("author"));
                        tvDate.setText((String) questionDetail.get("create_date"));
                        // 추천수, 답변수는 일단 하드코딩 또는 추후 구현
                        tvRecommendCount.setText("0");
                        tvAnswerCount.setText("답변 0개");
                    } else {
                        Toast.makeText(BoardDetailActivity.this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BoardDetailActivity.this, "응답 오류: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e("API", "게시글 로딩 실패: " + t.getMessage());
                Toast.makeText(BoardDetailActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}