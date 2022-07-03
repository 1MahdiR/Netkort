package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.Calendar;

import io.apptik.widget.MultiSlider;

public class SweepNetwork extends Thread {

    private Activity context;
    private TextView output_ui;
    private MultiSlider ip_range_1_seekbar;
    private MultiSlider ip_range_2_seekbar;
    private MultiSlider ip_range_3_seekbar;
    private MultiSlider ip_range_4_seekbar;
    private Calendar calendar;

    private volatile boolean isRunning = true;

    public void setParams(Activity context, TextView output_ui,
                          MultiSlider ip_range_1_seekbar, MultiSlider ip_range_2_seekbar,
                          MultiSlider ip_range_3_seekbar, MultiSlider ip_range_4_seekbar) {
        this.context = context;
        this.output_ui = output_ui;
        this.ip_range_1_seekbar = ip_range_1_seekbar;
        this.ip_range_2_seekbar = ip_range_2_seekbar;
        this.ip_range_3_seekbar = ip_range_3_seekbar;
        this.ip_range_4_seekbar = ip_range_4_seekbar;
    }

    public void kill() {
        this.isRunning = false;
    }

    public void reset() {
        this.isRunning = true;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void run() {
        this.calendar = Calendar.getInstance();

        int ip_1_value_1, ip_1_value_2;
        int ip_2_value_1, ip_2_value_2;
        int ip_3_value_1, ip_3_value_2;
        int ip_4_value_1, ip_4_value_2;

        try {

            ip_1_value_1 = this.ip_range_1_seekbar.getThumb(0).getValue();
            ip_1_value_2 = this.ip_range_1_seekbar.getThumb(1).getValue();

            ip_2_value_1 = this.ip_range_2_seekbar.getThumb(0).getValue();
            ip_2_value_2 = this.ip_range_2_seekbar.getThumb(1).getValue();

            ip_3_value_1 = this.ip_range_3_seekbar.getThumb(0).getValue();
            ip_3_value_2 = this.ip_range_3_seekbar.getThumb(1).getValue();

            ip_4_value_1 = this.ip_range_4_seekbar.getThumb(0).getValue();
            ip_4_value_2 = this.ip_range_4_seekbar.getThumb(1).getValue();

            this.context.runOnUiThread(() -> {
                output_ui.setText("Sweeping network in ");
                if (ip_1_value_1 == ip_1_value_2) {
                    output_ui.append(String.format("%d.", ip_1_value_1));
                } else {
                    output_ui.append(String.format("%d-%d.", ip_1_value_1, ip_1_value_2));
                }
                if (ip_2_value_1 == ip_2_value_2) {
                    output_ui.append(String.format("%d.", ip_2_value_1));
                } else {
                    output_ui.append(String.format("%d-%d.", ip_2_value_1, ip_2_value_2));
                }
                if (ip_3_value_1 == ip_3_value_2) {
                    output_ui.append(String.format("%d.", ip_3_value_1));
                } else {
                    output_ui.append(String.format("%d-%d.", ip_3_value_1, ip_3_value_2));
                }
                if (ip_4_value_1 == ip_4_value_2) {
                    output_ui.append(String.format("%d", ip_4_value_1));
                } else {
                    output_ui.append(String.format("%d-%d", ip_4_value_1, ip_4_value_2));
                }
                output_ui.append(" range\n");
                output_ui.append(Utility.getDateTime(this.calendar) + "\n\n");
            });

            for (int i = ip_1_value_1; i <= ip_1_value_2; i++) {
                for (int j = ip_2_value_1; j <= ip_2_value_2; j++) {
                    for (int k = ip_3_value_1; k <= ip_3_value_2; k++) {
                        for (int l = ip_4_value_1; l <= ip_4_value_2; l++) {

                            if (isRunning) {
                                String ip = String.format("%d.%d.%d.%d", i, j, k, l);
                                boolean ping_result = Utility.ping(ip, 1);
                                this.context.runOnUiThread(() -> {
                                    if (ping_result) {
                                        output_ui.append(String.format("ICMP packet received from (%s)", ip) + "\n");
                                    } else {
                                        SpannableString spannableString = new SpannableString(String.format("ICMP packet failed from (%s)", ip) + "\n");
                                        ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                                        spannableString.setSpan(red, 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        output_ui.append(spannableString);
                                    }
                                });

                            } else {

                                throw new InterruptedException();
                            }
                            sleep(100);
                        }
                    }
                }
            }

            this.context.runOnUiThread(() -> output_ui.append("\nSweep complete."));

        } catch (InterruptedException e) {
            this.context.runOnUiThread(() -> output_ui.append("\n------- stopped!"));
        } finally {
            this.context.runOnUiThread(() -> ((SweepNetworkActivity) context).enableUI());
        }
    }
}
