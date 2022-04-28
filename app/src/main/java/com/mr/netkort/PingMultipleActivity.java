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

import java.util.ArrayList;

public class PingMultipleActivity extends AppCompatActivity {

    LinearLayout hosts_layer;
    Button push_input;
    Button pop_input;
    SeekBar packet_timeout_seek_bar;
    TextView packet_timeout_text;

    public ArrayList<EditText> hosts;
    int hosts_len = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_multiple);

        hosts = new ArrayList<>();

        hosts_layer = findViewById(R.id.hosts);
        push_input = findViewById(R.id.push_input);
        pop_input = findViewById(R.id.pop_input);
        packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        packet_timeout_text = findViewById(R.id.packet_timeout_text);

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
    }
}