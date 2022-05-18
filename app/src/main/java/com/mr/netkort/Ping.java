package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.Calendar;

public class Ping extends Thread {

    private Activity context;
    private TextView output_ui;
    private String host_address;
    private String host_ip;
    private int timeout;
    private int packet_count;
    private Calendar calendar;

    private volatile boolean isRunning = true;

    public void setParams(Activity context, TextView output_ui, String host_address, int packet_count, int timeout) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = host_address;
        this.timeout = timeout;
        this.packet_count = packet_count;
    }

    public void kill() {
        this.isRunning = false;
    }

    public void reset() {
        this.isRunning = true;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void run() {
        this.calendar = Calendar.getInstance();
        int packets_received = 0;
        int packets_transmitted = 0;
        try {
            this.host_ip = Utility.getHostIp(this.host_address);

            if (this.host_ip != null) {
                this.context.runOnUiThread(() -> {
                    output_ui.setText(String.format("Pinging %s [%s]\n", this.host_address, this.host_ip));
                    output_ui.append(String.format("Timeout: %d", this.timeout));
                    output_ui.append(Utility.getDateTime(this.calendar) + "\n\n");
                });
                for (int i = 0; i < this.packet_count;) {

                    if (isRunning) {
                        boolean ping_result = Utility.ping(this.host_ip, this.timeout);
                        this.context.runOnUiThread(() -> {
                            if (ping_result) {
                                output_ui.append(String.format("ICMP packet received from (%s)", this.host_ip) + "\n");
                            } else {
                                SpannableString spannableString = new SpannableString("ICMP packet failed" + "\n");
                                ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                                spannableString.setSpan(red, 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                output_ui.append(spannableString);
                            }
                        });
                        if (ping_result) { packets_received++; }
                        packets_transmitted++;
                        sleep(1000);

                        if (packet_count < 101) { // Means it's not continuous
                            i++;
                        }
                    } else {

                        throw new InterruptedException();
                    }

                }
            } else {
                this.context.runOnUiThread(() -> {
                    output_ui.setText(String.format("Could not find host %s.", this.host_address) + "\n");
                });
            }

        } catch (InterruptedException e) {

        } finally {
            if (packets_transmitted > 0) {
                float pl = ((float)(packets_transmitted - packets_received) / packets_transmitted) * 100;
                int pt = packets_transmitted;
                int pr = packets_received;
                this.context.runOnUiThread(() -> {
                    output_ui.append("\n------------------------------\n\n");
                    output_ui.append(String.format("%d packets transmitted, %d received, %.2f%% packet loss",
                            pt, pr, pl) + "\n");
                });
            } else {
                this.context.runOnUiThread(() -> {
                    output_ui.append("\n------- stopped!");
                });
            }
            this.context.runOnUiThread(() -> {
                ((PingSingleActivity) context).enableUI();
            });
        }
    }
}
