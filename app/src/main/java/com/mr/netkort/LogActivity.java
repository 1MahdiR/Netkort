package com.mr.netkort;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogActivity extends AppCompatActivity {

    TextView console;
    LinearLayout log_list;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        console = findViewById(R.id.console_text);
        log_list = findViewById(R.id.log_list);

        SharedPreferences sharedPreferences = getSharedPreferences("logs", MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        Set<String> keys = map.keySet();
        List<String> sortedKeys = new ArrayList<>(keys);
        Collections.sort(sortedKeys);

        for (String key:sortedKeys) {
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 10);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setFocusable(true);
            linearLayout.setClickable(true);
            linearLayout.setBackgroundResource(R.drawable.icon_bg);
            linearLayout.setForeground(getDrawable(R.drawable.log_ripple));

            linearLayout.setOnClickListener(view -> {
                String text = sharedPreferences.getString(key, null);
                if (text != null) {
                    console.setText(text);
                }
            });

            linearLayout.setOnLongClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete log");
                builder.setMessage("Do want to delete this log?");
                builder.setPositiveButton("Delete it", (dialogInterface, i) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(key);
                    editor.apply();
                    log_list.removeView(view);
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());

                builder.show();
                return false;
            });

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(101, 137, 1);
            imageView.setLayoutParams(imageParams);
            imageView.setImageDrawable(getDrawable(R.drawable.log_icon));

            TextView textView = new TextView(this);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(220,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 2);
            textParams.gravity = Gravity.CENTER;
            textParams.setMargins(20, 0, 0, 0);
            textView.setLayoutParams(textParams);
            textView.setGravity(Gravity.CENTER);
            textView.setText(key);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            Typeface font = ResourcesCompat.getFont(this, R.font.opensans);
            textView.setTypeface(font);

            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            log_list.addView(linearLayout);
        }
    }
}