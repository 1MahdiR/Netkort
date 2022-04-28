package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LinearLayout ping_single_btn = findViewById(R.id.ping_single_btn);
        LinearLayout ping_multiple_btn = findViewById(R.id.ping_multiple_btn);

        ping_single_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PingSingleActivity.class);
            startActivity(intent);
        });

        ping_multiple_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PingMultipleActivity.class);
            startActivity(intent);
        });
    }
}