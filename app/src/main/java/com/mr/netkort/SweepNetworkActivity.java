package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.apptik.widget.MultiSlider;

public class SweepNetworkActivity extends AppCompatActivity {

    TextView console;

    MultiSlider ip_range_1_seekbar;
    MultiSlider ip_range_2_seekbar;
    MultiSlider ip_range_3_seekbar;
    MultiSlider ip_range_4_seekbar;

    TextView ip_range_1_text;
    TextView ip_range_2_text;
    TextView ip_range_3_text;
    TextView ip_range_4_text;

    Button start_network_sweep_btn;
    Button stop_network_sweep_btn;
    Button log_btn;

    public void enableUI() {
        stop_network_sweep_btn.setEnabled(false);
        start_network_sweep_btn.setEnabled(true);
        log_btn.setEnabled(true);
        ip_range_1_seekbar.setEnabled(true);
        ip_range_2_seekbar.setEnabled(true);
        ip_range_3_seekbar.setEnabled(true);
        ip_range_4_seekbar.setEnabled(true);
    }

    public void disableUI() {
        stop_network_sweep_btn.setEnabled(true);
        start_network_sweep_btn.setEnabled(false);
        log_btn.setEnabled(false);
        ip_range_1_seekbar.setEnabled(false);
        ip_range_2_seekbar.setEnabled(false);
        ip_range_3_seekbar.setEnabled(false);
        ip_range_4_seekbar.setEnabled(false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweep_network);

        console = findViewById(R.id.console_text);

        ip_range_1_seekbar = findViewById(R.id.ip1);
        ip_range_2_seekbar = findViewById(R.id.ip2);
        ip_range_3_seekbar = findViewById(R.id.ip3);
        ip_range_4_seekbar = findViewById(R.id.ip4);

        ip_range_1_text = findViewById(R.id.ip_text_1);
        ip_range_2_text = findViewById(R.id.ip_text_2);
        ip_range_3_text = findViewById(R.id.ip_text_3);
        ip_range_4_text = findViewById(R.id.ip_text_4);

        start_network_sweep_btn = findViewById(R.id.button_network_sweep);
        stop_network_sweep_btn = findViewById(R.id.button_stop);
        log_btn = findViewById(R.id.button_log);

        SweepNetwork sweepNetwork = new SweepNetwork();

        ip_range_1_seekbar.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {

            int value_1, value_2;
            value_1 = multiSlider.getThumb(0).getValue();
            value_2 = multiSlider.getThumb(1).getValue();

            if (value_1 == value_2) {
                ip_range_1_text.setText(String.format("%d", value_1));
            } else {
                ip_range_1_text.setText(String.format("%d-%d", value_1, value_2));
            }
        });

        ip_range_2_seekbar.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {

            int value_1, value_2;
            value_1 = multiSlider.getThumb(0).getValue();
            value_2 = multiSlider.getThumb(1).getValue();

            if (value_1 == value_2) {
                ip_range_2_text.setText(String.format("%d", value_1));
            } else {
                ip_range_2_text.setText(String.format("%d-%d", value_1, value_2));
            }
        });

        ip_range_3_seekbar.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {

            int value_1, value_2;
            value_1 = multiSlider.getThumb(0).getValue();
            value_2 = multiSlider.getThumb(1).getValue();

            if (value_1 == value_2) {
                ip_range_3_text.setText(String.format("%d", value_1));
            } else {
                ip_range_3_text.setText(String.format("%d-%d", value_1, value_2));
            }
        });

        ip_range_4_seekbar.setOnThumbValueChangeListener((multiSlider, thumb, thumbIndex, value) -> {

            int value_1, value_2;
            value_1 = multiSlider.getThumb(0).getValue();
            value_2 = multiSlider.getThumb(1).getValue();

            if (value_1 == value_2) {
                ip_range_4_text.setText(String.format("%d", value_1));
            } else {
                ip_range_4_text.setText(String.format("%d-%d", value_1, value_2));
            }
        });

        start_network_sweep_btn.setOnClickListener(view -> {

            Thread sweep_network_thread = new Thread(sweepNetwork);

            try {

                sweepNetwork.setParams(this, console, ip_range_1_seekbar, ip_range_2_seekbar,
                        ip_range_3_seekbar, ip_range_4_seekbar);

                disableUI();

                if (!sweep_network_thread.isAlive()) {
                    sweepNetwork.reset();
                    sweep_network_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_network_sweep_btn.setOnClickListener(view -> {
            sweepNetwork.kill();
            stop_network_sweep_btn.setEnabled(false);
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