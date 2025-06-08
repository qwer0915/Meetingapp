package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import com.example.meetingapp.ApiClient;
import com.example.meetingapp.ApiService;
import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    Button buttonTest,buttonUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        buttonUpload =findViewById(R.id.buttonUpload);

        buttonUpload.setOnClickListener(v -> {
            Intent intent = new Intent(this, BoardFormActivity.class);
            if (!isFinishing() && !isDestroyed()) {
                startActivity(intent);
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> finish(), 1000);
        });
        ApiService api = ApiClient.getClient().create(ApiService.class);

        //API 연결 테스트 Logcat에서 확인가능
        api.ping().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        Log.d("API", "연결 성공: " + body);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("API", "응답 실패: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("API", "서버 연결 실패: " + t.getMessage());
            }
        });
    }
}