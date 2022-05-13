package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Pair;
import android.widget.TextView;

public class Traceroute extends Thread {

    public enum Stat {
        NO_RESPONSE, TTL_EXCEEDED, REACHED, ERROR
    }

    private Activity context;
    private TextView output_ui;
    private String host_address;
    private String host_ip;
    private int timeout;
    private int packet_hop;

    private volatile boolean isRunning = true;

    public void setParams(Activity context, TextView output_ui, String host_address, int packet_hop, int timeout) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = host_address;
        this.timeout = timeout;
        this.packet_hop = packet_hop;
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
        try {
            this.host_ip = Utility.getHostIp(this.host_address);

            if (this.host_ip != null) {
                this.context.runOnUiThread(() -> {
                    output_ui.setText(String.format("Traceroute to %s [%s]\n", this.host_address, this.host_ip));
                });

                for (int i = 1; i <= this.packet_hop; i++) {

                    if (isRunning) {
                        final Pair<Traceroute.Stat, String> stat = Utility.traceroute_ping(this.host_ip, this.timeout, i);
                        int current_hop = i;
                        this.context.runOnUiThread(() -> {
                            switch (stat.first) {
                                case TTL_EXCEEDED:
                                case REACHED:
                                    output_ui.append(String.format("%d    %s", current_hop, stat.second) + "\n");
                                    break;
                                case NO_RESPONSE:
                                case ERROR:
                                    output_ui.append(String.format("%d    *", current_hop) + "\n");
                                    break;
                            }
                        });
                        if (i == this.packet_hop || stat.first == Stat.REACHED) {
                            this.context.runOnUiThread(() -> {
                                output_ui.append("\nTrace complete.");
                            });
                            break;
                        }
                        sleep(100);

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
            this.context.runOnUiThread(() -> {
                output_ui.append("\n------- stopped!");
            });
        } finally {
            this.context.runOnUiThread(() -> {
                ((TracerouteActivity) context).enableUI();
            });
        }
    }
}
