package com.mr.netkort;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class PingSingleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_single);

        SeekBar packet_count_seek_bar = findViewById(R.id.packet_count_seek_bar);
        TextView packet_count_text = findViewById(R.id.packet_count_text);
        SeekBar packet_timeout_seek_bar = findViewById(R.id.packet_timeout_seek_bar);
        TextView packet_timeout_text = findViewById(R.id.packet_timeout_text);

        packet_count_seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                packet_timeout_text.setText(String.format("%d00 millisecond", i+1));
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