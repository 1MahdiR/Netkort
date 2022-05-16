package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
    Button log_btn;

    public void enableUI() {
        stop_ping_btn.setEnabled(false);
        start_ping_btn.setEnabled(true);
        packet_count_seek_bar.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        log_btn.setEnabled(true);
        host_address.setEnabled(true);
    }

    public void disableUI() {
        stop_ping_btn.setEnabled(true);
        start_ping_btn.setEnabled(false);
        packet_count_seek_bar.setEnabled(false);
        packet_timeout_seek_bar.setEnabled(false);
        log_btn.setEnabled(false);
        host_address.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_single);

        Ping ping = new Ping();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        console = findViewById(R.id.console_text);
        host_address = findViewById(R.id.host_address);
        packet_count_seek_bar = findViewById(R.id.packet_count_seek_bar);
        packet_count_text = findViewById(R.id.packet_count_text);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);
        start_ping_btn = findViewById(R.id.button_ping);
        stop_ping_btn = findViewById(R.id.button_stop);
        log_btn = findViewById(R.id.button_log);

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

            Thread ping_thread = new Thread(ping);

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
                        });
                        builder2.setNegativeButton("Cancel", (dialogInterface2, i2) ->
                                dialogInterface2.cancel());

                        builder2.show();
                    } else {
                        editor.putString(key, txt);
                        editor.apply();
                    }
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

                builder.show();
            }
        });
    }
}