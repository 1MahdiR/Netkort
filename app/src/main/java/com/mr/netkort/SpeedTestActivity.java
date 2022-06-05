package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SpeedTestActivity extends AppCompatActivity {

    TextView console;
    ScrollView console_bg;
    Button start_speed_test;
    Button stop_ping_btn;
    Button log_btn;

    public void enableUI() {
        stop_ping_btn.setEnabled(false);
        start_speed_test.setEnabled(true);
        log_btn.setEnabled(true);
    }

    public void disableUI() {
        stop_ping_btn.setEnabled(true);
        start_speed_test.setEnabled(false);
        log_btn.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

        SpeedTest speedTest = new SpeedTest();

        console = findViewById(R.id.console_text);
        console_bg = findViewById(R.id.console);
        start_speed_test = findViewById(R.id.button_speedtest);
        stop_ping_btn = findViewById(R.id.button_stop);
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

        start_speed_test.setOnClickListener(view -> {

            Thread speedTest_thread = new Thread(speedTest);

            try {

                speedTest.setParams(this, console);
                disableUI();

                if (!speedTest_thread.isAlive()) {
                    speedTest.reset();
                    speedTest_thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stop_ping_btn.setOnClickListener(view -> {
            speedTest.kill();
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