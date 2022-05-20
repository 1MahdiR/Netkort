package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PortScanActivity extends AppCompatActivity {

    TextView console;
    ScrollView console_bg;
    TextView host_address;
    EditText port_start, port_end;

    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;

    Button start_port_scan, stop_port_scan, log_btn;

    public void enableUI() {
        stop_port_scan.setEnabled(false);
        start_port_scan.setEnabled(true);
        port_start.setEnabled(true);
        port_end.setEnabled(true);
        packet_timeout_seek_bar.setEnabled(true);
        host_address.setEnabled(true);
        log_btn.setEnabled(true);
    }

    public void disableUI() {
        stop_port_scan.setEnabled(true);
        start_port_scan.setEnabled(false);
        port_start.setEnabled(false);
        port_end.setEnabled(false);
        packet_timeout_seek_bar.setEnabled(false);
        host_address.setEnabled(false);
        log_btn.setEnabled(false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_scan);

        PortScan portScan = new PortScan();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        console = findViewById(R.id.console_text);
        console_bg = findViewById(R.id.console);
        host_address = findViewById(R.id.host_address);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);
        port_start = findViewById(R.id.port_start);
        port_end = findViewById(R.id.port_end);
        start_port_scan = findViewById(R.id.button_port_scan);
        stop_port_scan = findViewById(R.id.button_stop);
        log_btn = findViewById(R.id.button_log);

        SharedPreferences sharedPreferences_settings = getSharedPreferences("settings", MODE_PRIVATE);
        String theme = sharedPreferences_settings.getString("theme", "default");
        int size = sharedPreferences_settings.getInt("text_size", 14);

        switch (theme) {
            case "default":
                console_bg.setBackground(AppCompatResources.getDrawable(this, R.drawable.console_bg));
                console.setTypeface(ResourcesCompat.getFont(this, R.font.opensans));
                break;
            case "ubuntu":
                console_bg.setBackground(AppCompatResources.getDrawable(this, R.drawable.ubuntu_bg));
                console.setTypeface(ResourcesCompat.getFont(this, R.font.ubuntu));
                break;
            case "kali":
                console_bg.setBackground(AppCompatResources.getDrawable(this, R.drawable.kali_bg));
                console.setTypeface(ResourcesCompat.getFont(this, R.font.firacode));
                break;
        }

        console.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

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

            Thread portScan_thread = new Thread(portScan);

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
                int packet_timeout = packet_timeout_seek_bar.getProgress();

                portScan.setParams(this, console, host_address_str, packet_timeout, start, end);

                disableUI();

                if (!portScan_thread.isAlive()) {
                    portScan.reset();
                    portScan_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_port_scan.setOnClickListener(view -> {
            portScan.kill();
            stop_port_scan.setEnabled(false);
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