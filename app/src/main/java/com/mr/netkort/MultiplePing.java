package com.mr.netkort;

import android.app.Activity;
import android.widget.TextView;

import java.util.ArrayList;

public class MultiplePing extends Thread {

    private Activity context;
    private TextView output_ui;
    private ArrayList<String> host_address;
    private ArrayList<String> host_ip;
    private int timeout;

    public void setParams(Activity context, TextView output_ui, ArrayList<String> host_address, int timeout) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = host_address;
        this.host_ip = new ArrayList<>();
        this.timeout = timeout;
    }

    @Override
    public void run() {

        for (String item:this.host_address) {
            String ip = Utility.getHostIp(item);
            this.host_ip.add(ip);
        }

        try {
            this.context.runOnUiThread(() -> {
                output_ui.setText("");
            });
            for (int i = 0; i < this.host_ip.size(); i++) {
                String ip = this.host_ip.get(i);
                String host = this.host_address.get(i);
                if (this.host_ip.get(i) != null) {

                    boolean ping_result = Utility.ping(ip, this.timeout);
                    this.context.runOnUiThread(() -> {
                        if (ping_result) {
                            output_ui.append(String.format("ICMP packet received from %s (%s)", host, ip) + "\n");
                        } else {
                            output_ui.append(String.format("ICMP packet failed from %s (%s)", host, ip) + "\n");
                        }
                    });
                    sleep(1000);

                } else {
                    this.context.runOnUiThread(() -> {
                        output_ui.append(String.format("Could not find host %s.", host) + "\n");
                    });
                }
            }

        } catch (InterruptedException e) {

        } finally {
            this.context.runOnUiThread(() -> {
                output_ui.append("\n-------");
            });
        }
    }
}
