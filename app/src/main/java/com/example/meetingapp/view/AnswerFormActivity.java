package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class AnswerFormActivity extends BaseActivity {
    Button board_title, btn_submit;
    EditText ans_content;
    ApiService api;
    int questionId, answerId;
    String mode;
    String username;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_form);
        setupToolbar();

        board_title = findViewById(R.id.board_title);
        btn_submit = findViewById(R.id.btn_submit);
        ans_content = findViewById(R.id.ans_content);

        api = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        intent = getIntent();
        mode = intent.getStringExtra("mode");
        questionId = intent.getIntExtra("questionId", -1);
        answerId = intent.getIntExtra("answerId", -1);
        String content = intent.getStringExtra("content");
        String titleBoard = intent.getStringExtra("title");

        username=getLoggedInUsername();

        board_title.setText(titleBoard);
        if (questionId == -1 && !"edit".equals(mode)) {
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if ("edit".equals(mode)) {
            ans_content.setText(content);
            board_title.setText(titleBoard);
            btn_submit.setText("수정");
        } else {
            btn_submit.setText("등록");
        }

        btn_submit.setOnClickListener(v -> {
            String inputContent = ans_content.getText().toString().trim();

            if (inputContent.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("content", inputContent);
            data.put("author", username);

            if ("edit".equals(mode)) {
                updateAnswer( data, inputContent);
            } else {
                createAnswer(questionId, data);
            }
        });
    }

    private void createAnswer(int questionId, Map<String, Object> data) {
        api.createAnswer(questionId, data).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AnswerFormActivity.this, "답변이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AnswerFormActivity.this, "등록 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AnswerFormActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAnswer(Map<String, Object> data, String updatedContent) {
        data.put("answerId", answerId);
        data.put("username", username);
        data.put("content",ans_content.getText().toString().trim());;
        data.put("questionId",questionId);
        Log.d("AnswerForm", "Sending update request with data: " + data.toString());

        api.updateAnswer(data).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Log.d("AnswerForm", "Update response received: " + response.code());
                if (response.isSuccessful()) {
                    hideKeyboard();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("answerId", answerId);
                    resultIntent.putExtra("updatedContent", updatedContent);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(AnswerFormActivity.this, "수정 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AnswerFormActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null && view.isAttachedToWindow()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

