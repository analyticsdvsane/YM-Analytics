package com.example.sdk_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yuktamedia.analytics.Analytics;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Analytics.with(this).track("SDK_Deployment");
    }
}