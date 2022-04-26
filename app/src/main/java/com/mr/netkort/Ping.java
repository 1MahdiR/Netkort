package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Pair;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Ping extends Thread {

    private volatile Pair<Boolean, String> pair;
    private Activity context;
    private TextView output_ui;
    private String host_address;
    private String host_ip;
    private int timeout;
    private int packet_count;

    public static String getHostIp(String host_address) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            InetAddress inetAddr = InetAddress.getByName(host_address);

            byte[] addr = inetAddr.getAddress();

            String ipAddr = "";
            for (int i = 0; i < addr.length; i++) {
                if (i > 0) {
                    ipAddr += ".";
                }
                ipAddr += addr[i] & 0xFF;
            }

            return ipAddr;

        } catch (UnknownHostException e) {
            return null;
        }
    }

    public void setParams(Activity context, TextView output_ui, HashMap<String, String> args) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = args.get("host_address");
        this.host_ip = getHostIp(this.host_address);
        this.timeout = Integer.parseInt(args.getOrDefault("timeout", "1"));
        this.packet_count = Integer.parseInt(args.getOrDefault("count", "1"));
    }

    public void ping() {

        try {
            @SuppressLint("DefaultLocale")
            Process p = Runtime.getRuntime().exec(String.format("/system/bin/ping -c 1 -W %s %s", this.timeout, this.host_ip));

            // For reading ping's output
            /*
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            */

            int result = p.waitFor();

            if (result == 0) {
                this.pair = new Pair<>(true, String.format("ICMP packet received from (%s)", host_ip));
            } else {
                this.pair = new Pair<>(false, "ICMP packet failed");
            }
            return;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }

        this.pair = new Pair<>(false, "ICMP packet failed");
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
                    ping();
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
