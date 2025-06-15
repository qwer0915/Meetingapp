package com.example.meetingapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;
import com.example.meetingapp.entity.AnswerAdapter;
import com.example.meetingapp.entity.AnswerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailActivity extends BaseActivity {
    private TextView tvTitle, tvContent, tvAuthor, tvDate, tvRecommendCount, tvAnswerCount;
    private EditText etAnswer;
    private Button btnRecommend, btnModify, btnDelete, btnSubmitAnswer;
    private RecyclerView rvAnswers;
    private AnswerAdapter answerAdapter;
    ApiService api;
    private int questionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);
        setupToolbar();

        //게시글 유효성 검증
        questionId = getIntent().getIntExtra("id", -1); // 전달된 id 받기
        if (questionId != -1) {
            loadQuestionDetail(questionId); // 전달받은 id로 데이터 요청
        } else {
            Toast.makeText(this, "유효하지 않은 게시글입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        initViews();
        //
        btnModify.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoardFormActivity.class);
            intent.putExtra("mode", "edit");
            intent.putExtra("id", questionId);
            intent.putExtra("title", tvTitle.getText().toString());
            intent.putExtra("content", tvContent.getText().toString());
            editLauncher.launch(intent);
        });
        btnDelete.setOnClickListener(v -> {
            api = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String username = prefs.getString("username", null);
            if (username == null) {
                Toast.makeText(this, "로그인 후 삭제할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, Object> body = new HashMap<>();
            body.put("author", username);
            api.deleteQuestion(questionId, body).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(BoardDetailActivity.this, "삭제 완료", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(BoardDetailActivity.this, "삭제 실패: 권한이 없거나 서버 오류", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(BoardDetailActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnSubmitAnswer.setOnClickListener(v->{
            Context context = v.getContext();
            Intent intent = new Intent(context, AnswerFormActivity.class);
            intent.putExtra("title", tvTitle.getText().toString());
            intent.putExtra("questionId", questionId);
            context.startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        int questionId = getIntent().getIntExtra("id", -1);
        if (questionId != -1) {
            loadQuestionDetail(questionId);
        }
    }
    private void initViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvAuthor = findViewById(R.id.tv_author);
        tvDate = findViewById(R.id.tv_date);
        tvRecommendCount = findViewById(R.id.tv_recommend_count);
        tvAnswerCount = findViewById(R.id.tv_answer_count);
        btnRecommend = findViewById(R.id.btn_recommend);
        btnModify = findViewById(R.id.btn_modify);
        btnDelete = findViewById(R.id.btn_delete);
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
        rvAnswers = findViewById(R.id.rv_answers);
        rvAnswers.setLayoutManager(new LinearLayoutManager(this));
        api= ApiClient.getClient(this).create(ApiService.class);
        answerAdapter = new AnswerAdapter(this, api,questionId);
        rvAnswers.setAdapter(answerAdapter);
    }

    private void loadQuestionDetail(int id) {
        api = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        api.getQuestionDetail(id).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Map<String, Object> root = response.body();
                    Map<String, Object> questionDetail = (Map<String, Object>) root.get("questionDetail");
                    Object answerObj = root.get("answerList");

                    if (questionDetail != null) {
                        tvTitle.setText((String) questionDetail.get("subject"));
                        tvContent.setText((String) questionDetail.get("content"));
                        tvAuthor.setText((String) questionDetail.get("author"));
                        tvDate.setText((String) questionDetail.get("create_date"));
                        // 추천수, 답변수는 일단 하드코딩 또는 추후 구현
                        tvRecommendCount.setText("0");
                    }
                    if (answerObj instanceof List<?>) {
                        List<?> rawList = (List<?>) answerObj;
                        List<AnswerItem> answers = new ArrayList<>();

                        for (Object o : rawList) {
                            if (o instanceof Map) {
                                Map<String, Object> ans = (Map<String, Object>) o;
                                String author = String.valueOf(ans.get("author"));
                                String content = String.valueOf(ans.get("content"));
                                String date = String.valueOf(ans.get("create_date"));
                                answers.add(new AnswerItem(id,author, date, content));
                            }
                        }

                        tvAnswerCount.setText("답변 " + answers.size() + "개");
                        answerAdapter.setItems(answers);
                    }
                    else {
                        Log.e("API", "answerList 파싱 실패: " + (answerObj == null ? "null" : answerObj.getClass().getName()));
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
    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadQuestionDetail(questionId);
                }
            });
}