package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;
import com.example.meetingapp.entity.PagingMap;
import com.example.meetingapp.entity.QuestionAdapter;
import com.example.meetingapp.entity.QuestionItem;
import com.example.meetingapp.entity.QuestionResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {
    ApiService api;
    Button buttonUpload,btnSearch,btnPageNow,btnPrev,btnNext;
    EditText etSearch;
    QuestionAdapter questionAdapter  = new QuestionAdapter(new ArrayList<>());
    Integer pageNow=1;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        buttonUpload =findViewById(R.id.buttonUpload);
        btnSearch =findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        btnPageNow= findViewById(R.id.btnPageNow);
        btnPrev= findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        recyclerView = findViewById(R.id.recyclerView);
        buttonUpload.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoardFormActivity.class);
            if (!isFinishing() && !isDestroyed()) {
                startActivity(intent);
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 1000);
        });
        api= ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        //API 연결 테스트 Logcat에서 확인가능

        recyclerView.setAdapter(questionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(v->{
            String keyword =etSearch.getText().toString();
            loadQuestions(api, keyword, 1);
        });
        loadQuestions(api, "", 1);


    }
    @Override
    protected void onResume() {
        super.onResume();
        loadQuestions(api, "", 1);
    }
    private void loadQuestions(ApiService api, String keyword, int pageNum) {
        api.getQuestions(keyword, pageNum).enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                if (response.isSuccessful()) {
                    List<QuestionItem> questions = response.body().questionList;
                    questionAdapter.updateData(questions);
                    updatePaginationButtons(response.body().pagingMap);
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
    private void updatePaginationButtons(PagingMap pagingMap) {
        btnPrev.setOnClickListener(v -> loadQuestions(api, etSearch.getText().toString(), pageNow-1));
        btnNext.setOnClickListener(v -> loadQuestions(api, etSearch.getText().toString(), pageNow+1));
        // 버튼에 setOnClickListener로 pageNum 넘기기 처리
        // 예: btnPage1.setOnClickListener(v -> loadQuestions(api, currentKeyword, 1));
    }
}