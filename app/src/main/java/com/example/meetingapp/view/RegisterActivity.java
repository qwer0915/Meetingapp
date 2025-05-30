package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;

public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupToolbar();
    }
}