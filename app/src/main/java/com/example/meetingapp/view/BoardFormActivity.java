package com.example.meetingapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.meetingapp.BaseActivity;
import com.example.meetingapp.R;

public class BoardFormActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_form);
        setupToolbar();

    }
}