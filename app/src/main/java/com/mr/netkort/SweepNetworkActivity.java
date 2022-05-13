package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import io.apptik.widget.MultiSlider;

public class SweepNetworkActivity extends AppCompatActivity {

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

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sweep_network);

        ip_range_1_seekbar = findViewById(R.id.ip1);
        ip_range_2_seekbar = findViewById(R.id.ip2);
        ip_range_3_seekbar = findViewById(R.id.ip3);
        ip_range_4_seekbar = findViewById(R.id.ip4);

        ip_range_1_text = findViewById(R.id.ip_text_1);
        ip_range_2_text = findViewById(R.id.ip_text_2);
        ip_range_3_text = findViewById(R.id.ip_text_3);
        ip_range_4_text = findViewById(R.id.ip_text_4);

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
    }
}