package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Pair;
import android.widget.TextView;

import java.util.HashMap;

public class Ping extends Thread {

    private volatile Pair<Boolean, String> pair;
    private Activity context;
    private TextView output_ui;
    private String host_address;
    private String host_ip;
    private int timeout;
    private int packet_count;

    public void setParams(Activity context, TextView output_ui, HashMap<String, String> args) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = args.get("host_address");
        this.host_ip = Utility.getHostIp(this.host_address);
        this.timeout = Integer.parseInt(args.getOrDefault("timeout", "1"));
        this.packet_count = Integer.parseInt(args.getOrDefault("count", "1"));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void run() {
        int packets_received = 0;
        int packets_transmitted = 0;
        try {
            if (this.host_ip != null) {
                this.context.runOnUiThread(() -> {
                    output_ui.setText(String.format("Pinging %s [%s]\n", this.host_address, this.host_ip));
                });
                for (int i = 0; i < this.packet_count; i++) {
                    this.pair = Utility.ping(this.host_ip, this.host_address, this.timeout, false);
                    this.context.runOnUiThread(() -> {
                        output_ui.append(this.pair.second + "\n");
                    });
                    if (this.pair.first) { packets_received++; }
                    packets_transmitted++;
                    sleep(1000);
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
        }
    }
}
