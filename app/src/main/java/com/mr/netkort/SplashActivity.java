package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        if (!sharedPreferences.contains("init")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("init", false);
            editor.putString("theme", "default");
            editor.putInt("text_size", 14);
            editor.apply();
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainMenu.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}