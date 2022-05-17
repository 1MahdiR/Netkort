package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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
    Button log_btn;

    public ArrayList<EditText> hosts;
    int hosts_len = 0;

    public void enableUI() {
        stop_ping_btn.setEnabled(false);
        start_ping_btn.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        push_input.setEnabled(true);
        log_btn.setEnabled(true);

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
        log_btn.setEnabled(false);

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
        log_btn = findViewById(R.id.button_log);

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
            (hosts_layer).removeView(et);
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

        log_btn.setOnClickListener(view -> {
            String txt = console.getText().toString();
            if (!txt.trim().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Save log");
                final EditText input = new EditText(this);
                final LinearLayout linearLayout = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(60, 10, 60, 10);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Enter a name for log");
                input.setLayoutParams(layoutParams);
                input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(22) });
                input.setSingleLine();

                linearLayout.addView(input);
                builder.setView(linearLayout);

                builder.setPositiveButton("OK", (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("logs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String key = input.getText().toString().trim();
                    if (sharedPreferences.contains(key)) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                        builder2.setTitle("Warning");
                        builder2.setMessage("There is already a log with this name.\n" +
                                "Do you want to overwrite it?");
                        builder2.setPositiveButton("Yes", (dialogInterface2, i2) -> {
                            editor.putString(key, txt);
                            editor.apply();
                            Toast.makeText(this, "Log has been saved.", Toast.LENGTH_SHORT).show();
                        });
                        builder2.setNegativeButton("Cancel", (dialogInterface2, i2) ->
                                dialogInterface2.cancel());

                        builder2.show();
                    } else {
                        editor.putString(key, txt);
                        editor.apply();
                        Toast.makeText(this, "Log has been saved.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

                builder.show();
            }
        });
    }
}