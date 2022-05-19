package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    TextView console;
    ScrollView console_bg;
    SeekBar text_size;
    TextView default_theme_btn;
    TextView ubuntu_theme_btn;
    TextView kali_theme_btn;
    Button save_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        console = findViewById(R.id.console_text);
        console_bg = findViewById(R.id.console);
        text_size = findViewById(R.id.text_size);
        default_theme_btn = findViewById(R.id.default_theme_btn);
        ubuntu_theme_btn = findViewById(R.id.ubuntu_theme_btn);
        kali_theme_btn = findViewById(R.id.kali_theme_btn);
        save_btn = findViewById(R.id.button_save);
        console.setMovementMethod(LinkMovementMethod.getInstance());

        text_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                console.setTextSize(TypedValue.COMPLEX_UNIT_SP, i * 2 + 12);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        default_theme_btn.setOnClickListener(view -> {
            console_bg.setBackground(AppCompatResources.getDrawable(this, R.drawable.console_bg));
            console.setTypeface(ResourcesCompat.getFont(this, R.font.opensans));
        });

        ubuntu_theme_btn.setOnClickListener(view -> {
            console_bg.setBackground(AppCompatResources.getDrawable(this, R.drawable.ubuntu_bg));
            console.setTypeface(ResourcesCompat.getFont(this, R.font.ubuntu));
        });

        kali_theme_btn.setOnClickListener(view -> {
            console_bg.setBackground(AppCompatResources.getDrawable(this, R.drawable.kali_bg));
            console.setTypeface(ResourcesCompat.getFont(this, R.font.firacode));
        });
    }
}