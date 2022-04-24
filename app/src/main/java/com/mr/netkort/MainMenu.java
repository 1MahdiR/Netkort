package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainMenu extends AppCompatActivity {

    private String ping_single_host_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        LinearLayout ping_single_btn = findViewById(R.id.ping_single_btn);


        ping_single_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PingSingleActivity.class);
            startActivity(intent);
        });
    }
}