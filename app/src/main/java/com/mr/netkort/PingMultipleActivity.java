package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PingMultipleActivity extends AppCompatActivity {

    LinearLayout hosts_layer;
    Button push_input;
    Button pop_input;
    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;

    Button start_ping_btn;
    Button stop_ping_btn;

    public ArrayList<EditText> hosts;
    int hosts_len = 0;

    public void enableUI() {
        stop_ping_btn.setEnabled(false);
        start_ping_btn.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        push_input.setEnabled(true);
        if (hosts_len > 0) { pop_input.setEnabled(true); }

        for (EditText et:hosts) {
            et.setEnabled(true);
        }
    }

    public void disableUI() {
        stop_ping_btn.setEnabled(true);
        start_ping_btn.setEnabled(false);
        packet_timeout_seek_bar.setEnabled(false);
        push_input.setEnabled(false);
        pop_input.setEnabled(false);

        for (EditText et:hosts) {
            et.setEnabled(false);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_multiple);

        hosts = new ArrayList<>();

        MultiplePing ping = new MultiplePing();

        TextView console = findViewById(R.id.console_text);

        hosts_layer = findViewById(R.id.hosts);
        push_input = findViewById(R.id.push_input);
        pop_input = findViewById(R.id.pop_input);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);

        start_ping_btn = findViewById(R.id.button_ping);
        stop_ping_btn = findViewById(R.id.button_stop);

        push_input.setOnClickListener((view) -> {
            EditText et = new EditText(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            int den = (int) getResources().getDisplayMetrics().density;
            lp.setMargins(10 * den, 10 * den, 10 * den, 0);
            et.setLayoutParams(lp);

            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setHint("Host IP or address");
            et.setMinHeight(den * 48);
            et.setTextColor(ContextCompat.getColor(this, R.color.white));
            et.setHintTextColor(ContextCompat.getColor(this, R.color.gray_500));

            hosts_layer.addView(et, hosts_len++);
            hosts.add(et);
            if (hosts_len > 0) { pop_input.setEnabled(true); }
        });

        pop_input.setOnClickListener((view) -> {
            EditText et = hosts.get(--hosts_len);
            hosts.remove(et);
            ((ViewGroup) hosts_layer).removeView(et);
            if (hosts_len == 0) { view.setEnabled(false); }
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

            Thread ping_thread = new Thread(ping);

            ArrayList<String> host_addresses = new ArrayList<>();
            for (TextView et:hosts) {
                String temp = et.getText().toString().trim();
                if (!temp.isEmpty()) {
                    host_addresses.add(temp);
                } else {
                    Toast.makeText(this, "Fill all 'host address' fields", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            try {

                ping.setParams(this, console, host_addresses, packet_timeout_seek_bar.getProgress()+1);

                disableUI();

                if (!ping_thread.isAlive()) {
                    ping.reset();
                    ping_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_ping_btn.setOnClickListener(view -> {
            ping.kill();
            stop_ping_btn.setEnabled(false);
        });
    }
}