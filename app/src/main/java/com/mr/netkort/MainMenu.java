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
        LinearLayout port_scan_btn = findViewById(R.id.port_scan_btn);
        LinearLayout traceroute_btn = findViewById(R.id.traceroute_btn);
        LinearLayout speedTest_btn = findViewById(R.id.speed_test_btn);
        LinearLayout sweep_network_btn = findViewById(R.id.sweep_network_btn);
        LinearLayout logs_btn = findViewById(R.id.logs_btn);
        LinearLayout settings_btn = findViewById(R.id.settings_btn);

        ping_single_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PingSingleActivity.class);
            startActivity(intent);
        });

        ping_multiple_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PingMultipleActivity.class);
            startActivity(intent);
        });

        port_scan_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PortScanActivity.class);
            startActivity(intent);
        });

        traceroute_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, TracerouteActivity.class);
            startActivity(intent);
        });

        speedTest_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SpeedTestActivity.class);
            startActivity(intent);
        });

        sweep_network_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SweepNetworkActivity.class);
            startActivity(intent);
        });

        logs_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        });

        settings_btn.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}