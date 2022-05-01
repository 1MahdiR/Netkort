package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PingSingleActivity extends AppCompatActivity {

    TextView console;
    TextView host_address;
    SeekBar packet_count_seek_bar;
    TextView packet_count_text;
    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;
    Button start_ping_btn;
    Button stop_ping_btn;

    public void enableUI() {
        stop_ping_btn.setEnabled(false);
        start_ping_btn.setEnabled(true);
        packet_count_seek_bar.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        host_address.setEnabled(true);
    }

    public void disableUI() {
        stop_ping_btn.setEnabled(true);
        start_ping_btn.setEnabled(false);
        packet_count_seek_bar.setEnabled(false);
        packet_timeout_seek_bar.setEnabled(false);
        host_address.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_single);

        Ping ping = new Ping();
        Thread ping_thread = new Thread(ping);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        console = findViewById(R.id.console_text);
        host_address = findViewById(R.id.host_address);
        packet_count_seek_bar = findViewById(R.id.packet_count_seek_bar);
        packet_count_text = findViewById(R.id.packet_count_text);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);
        start_ping_btn = findViewById(R.id.button_ping);
        stop_ping_btn = findViewById(R.id.button_stop);

        packet_count_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (i) {
                    case 0:
                        packet_count_text.setText("1 packet");
                        break;
                    case 100:
                        packet_count_text.setText("continuous");
                        break;
                    default:
                        packet_count_text.setText(String.format("%d packets", i+1));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        packet_timeout_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                packet_timeout_text.setText(String.format("%d second", i+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        start_ping_btn.setOnClickListener(view -> {

            String host_address_str = host_address.getText().toString().trim();
            if (host_address_str.isEmpty()) {
                Toast.makeText(this, "host address can not be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int packet_timeout = packet_timeout_seek_bar.getProgress() + 1;
                int packet_count = packet_count_seek_bar.getProgress() + 1;

                ping.setParams(this, console, host_address_str, packet_count, packet_timeout);

                disableUI();

                if (!ping_thread.isAlive()) {
                    ping_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_ping_btn.setOnClickListener(view -> {
            ping.interrupt();
            ping_thread.interrupt();
            enableUI();
        });
    }
}