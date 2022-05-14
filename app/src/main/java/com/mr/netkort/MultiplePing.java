package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MultiplePing extends Thread {

    private Activity context;
    private TextView output_ui;
    private ArrayList<String> host_address;
    private ArrayList<String> host_ip;
    private int timeout;
    private Calendar calendar;

    private volatile boolean isRunning = true;

    public void setParams(Activity context, TextView output_ui, ArrayList<String> host_address, int timeout) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = host_address;
        this.host_ip = new ArrayList<>();
        this.timeout = timeout;
    }

    public void kill() {
        isRunning = false;
    }

    public void reset() {
        isRunning = true;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void run() {
        this.calendar = Calendar.getInstance();

        for (String item:this.host_address) {
            String ip = Utility.getHostIp(item);
            this.host_ip.add(ip);
        }

        try {
            this.context.runOnUiThread(() -> {
                if (this.host_address.size() > 1) {
                    output_ui.setText(String.format("Pinging %d hosts\n", host_address.size()));
                } else {
                    output_ui.setText("Pinging 1 host\n");
                }
                output_ui.append(Utility.getDateTime(this.calendar) + "\n\n");
            });
            for (int i = 0; i < this.host_ip.size(); i++) {

                if (!isRunning) {
                    throw new InterruptedException();
                }

                String ip = this.host_ip.get(i);
                String host = this.host_address.get(i);
                if (this.host_ip.get(i) != null) {

                    boolean ping_result = Utility.ping(ip, this.timeout);
                    this.context.runOnUiThread(() -> {
                        if (ping_result) {
                            output_ui.append(String.format("ICMP packet received from %s (%s)", host, ip) + "\n");
                        } else {
                            String failed_message = String.format("ICMP packet failed from %s (%s)", host, ip) + "\n";
                            SpannableString spannableString = new SpannableString(failed_message);
                            ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                            spannableString.setSpan(red, 12, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            output_ui.append(spannableString);
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
                ((PingMultipleActivity) context).enableUI();
            });
        }
    }
}
