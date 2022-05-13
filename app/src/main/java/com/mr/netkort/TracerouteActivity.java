package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

        Traceroute traceroute = new Traceroute();

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
                packet_hop_text.setText(String.format("%d hops", i+1));
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

        start_traceroute_btn.setOnClickListener(view -> {

            Thread traceroute_thread = new Thread(traceroute);

            String host_address_str = host_address.getText().toString().trim();
            if (host_address_str.isEmpty()) {
                Toast.makeText(this, "host address can not be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int packet_hops = packet_hop_seek_bar.getProgress() + 1;
                int packet_timeout = packet_timeout_seek_bar.getProgress() + 1;

                traceroute.setParams(this, console, host_address_str, packet_hops, packet_timeout);

                disableUI();

                if (!traceroute_thread.isAlive()) {
                    traceroute.reset();
                    traceroute_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_traceroute_btn.setOnClickListener(view -> {
            traceroute.kill();
            stop_traceroute_btn.setEnabled(false);
        });
    }
}