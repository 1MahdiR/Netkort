package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PortScanActivity extends AppCompatActivity {

    TextView console;
    TextView host_address;
    EditText port_start, port_end;

    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;

    Button start_port_scan, stop_port_scan;

    public void enableUI() {
        stop_port_scan.setEnabled(false);
        start_port_scan.setEnabled(true);
        port_start.setEnabled(true);
        port_end.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        host_address.setEnabled(true);
    }

    public void disableUI() {
        stop_port_scan.setEnabled(true);
        start_port_scan.setEnabled(false);
        port_start.setEnabled(false);
        port_end.setEnabled(false);
        packet_timeout_seek_bar.setEnabled(false);
        host_address.setEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_scan);

        PortScan portScan = new PortScan();
        Thread portScan_thread = new Thread(portScan);

        console = findViewById(R.id.console_text);
        host_address = findViewById(R.id.host_address);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);
        port_start = findViewById(R.id.port_start);
        port_end = findViewById(R.id.port_end);
        start_port_scan = findViewById(R.id.button_port_scan);
        stop_port_scan = findViewById(R.id.button_stop);

        packet_timeout_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 0) {
                    packet_timeout_text.setText("0.5 second");
                } else {
                    packet_timeout_text.setText(String.format("%d second", i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        start_port_scan.setOnClickListener(view -> {

            int start = Integer.parseInt(port_start.getText().toString());
            int end = Integer.parseInt(port_end.getText().toString());

            if (start > 65535) { start = 65535; }
            if (start <= 0) { start = 1; }

            if (end > 65535) { end = 65535; }
            if (end <= 0) { end = 1; }

            if (start > end) {
                end = start;
            }

            port_start.setText(Integer.toString(start));
            port_end.setText(Integer.toString(end));

            String host_address_str = host_address.getText().toString().trim();
            if (host_address_str.isEmpty()) {
                Toast.makeText(this, "host address can not be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int packet_timeout = packet_timeout_seek_bar.getProgress() + 1;

                portScan.setParams(this, console, host_address_str, packet_timeout, start, end);

                disableUI();

                if (!portScan_thread.isAlive()) {
                    portScan_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_port_scan.setOnClickListener(view -> {
            portScan.interrupt();
            portScan_thread.interrupt();
            stop_port_scan.setEnabled(false);
        });
    }
}