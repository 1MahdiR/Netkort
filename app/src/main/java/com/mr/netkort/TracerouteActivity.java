package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class TracerouteActivity extends AppCompatActivity {

    TextView console;
    TextView host_address;
    SeekBar packet_hop_seek_bar;
    TextView packet_hop_text;
    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;
    Button start_traceroute_btn;
    Button stop_traceroute_btn;

    public void enableUI() {
        stop_traceroute_btn.setEnabled(false);
        start_traceroute_btn.setEnabled(true);
        packet_hop_seek_bar.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        host_address.setEnabled(true);
    }

    public void disableUI() {
        stop_traceroute_btn.setEnabled(true);
        start_traceroute_btn.setEnabled(false);
        packet_hop_seek_bar.setEnabled(false);
        packet_timeout_seek_bar.setEnabled(false);
        host_address.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traceroute);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        console = findViewById(R.id.console_text);
        host_address = findViewById(R.id.host_address);
        packet_hop_seek_bar = findViewById(R.id.packet_hop_seek_bar);
        packet_hop_text = findViewById(R.id.packet_hop_text);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);
        start_traceroute_btn = findViewById(R.id.button_traceroute);
        stop_traceroute_btn = findViewById(R.id.button_stop);

        packet_hop_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                packet_hop_text.setText(String.format("%d packets", i+10));
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
    }
}