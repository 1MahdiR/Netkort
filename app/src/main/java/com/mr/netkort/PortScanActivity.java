package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class PortScanActivity extends AppCompatActivity {

    EditText port_start, port_end;

    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_scan);

        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);
        port_start = findViewById(R.id.port_start);
        port_end = findViewById(R.id.port_end);

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

        port_start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println(b);
            }
        });

    }
}