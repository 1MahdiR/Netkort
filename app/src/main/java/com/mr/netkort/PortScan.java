package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Calendar;


public class PortScan extends Thread {

    private Activity context;
    private TextView output_ui;
    private String host_address;
    private String host_ip;
    private int timeout;
    private int port_start;
    private int port_end;
    private Calendar calendar;

    private volatile boolean isRunning;

    public void setParams(Activity context, TextView output_ui, String host_address, int timeout,
                          int port_start, int port_end) {
        this.context = context;
        this.output_ui = output_ui;
        this.host_address = host_address;
        this.timeout = timeout;
        this.port_start = port_start;
        this.port_end = port_end;
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

        try {
            this.host_ip = Utility.getHostIp(this.host_address);

            if (this.host_ip != null) {
                this.context.runOnUiThread(() -> {
                    output_ui.setText(String.format("Checking ports %d to %d on %s [%s]\n",
                            this.port_start, this.port_end, this.host_address, this.host_ip));
                    if (this.timeout == 0) {
                        output_ui.append("Timeout: 0.5\n");
                    } else {
                        output_ui.append(String.format("Timeout: %d\n", this.timeout));
                    }
                    output_ui.append(Utility.getDateTime(this.calendar) + "\n\n");
                });
                for (int port = this.port_start; port <= port_end; port++) {

                    if (!isRunning) {
                        throw new InterruptedException();
                    }

                    boolean isPortOpen = false;
                    InetAddress inetAddress = InetAddress.getByName(host_ip);
                    Socket socket = null;
                    try {
                        SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
                        socket = new Socket();
                        if (this.timeout == 0) {
                            socket.connect(socketAddress, 500);
                        } else {
                            socket.connect(socketAddress, timeout * 1000);
                        }
                        isPortOpen = true;
                    } catch (Exception e) {

                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (Exception e) {

                            }
                        }
                    }

                    final int temp_port = port;
                    final boolean temp_isPortOpen = isPortOpen;
                    this.context.runOnUiThread(() -> {
                        if (!temp_isPortOpen) {
                            output_ui.append(String.format("port %d: CLOSED", temp_port) + "\n");
                        } else {
                            SpannableString spannableString = new SpannableString(String.format("port %d: OPEN" + "\n", temp_port));
                            ForegroundColorSpan green = new ForegroundColorSpan(Color.GREEN);
                            spannableString.setSpan(green, 9, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            output_ui.append(spannableString);
                        }
                    });
                }
            } else {
                this.context.runOnUiThread(() -> {
                    output_ui.setText(String.format("Could not find host %s.", this.host_address) + "\n");
                });
            }

        } catch (Exception e) {

        } finally {
            this.context.runOnUiThread(() -> {
                output_ui.append("\n-------");
                ((PortScanActivity) context).enableUI();
            });
        }
    }


}
