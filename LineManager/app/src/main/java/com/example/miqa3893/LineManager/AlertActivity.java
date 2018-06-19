package com.example.miqa3893.LineManager;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_alert);
        //ActionBarの変更
        ActionBar actionBar = getActionBar();
        setTitle("警告画面");
    }
}
