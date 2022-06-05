package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class SpeedTest extends Thread {

    private Activity context;
    private TextView output_ui;
    private Calendar calendar;
    private URL url;

    private volatile boolean isRunning = true;

    public void setParams(Activity context, TextView output_ui) {
        this.context = context;
        this.output_ui = output_ui;
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
        InputStream input = null;
        HttpURLConnection connection = null;

        try {
            this.context.runOnUiThread(() -> {
                output_ui.setText("Testing download throughput\n");
                output_ui.append(Utility.getDateTime(this.calendar) + "\n\nDownload rate: ");
            });

            url = new URL("https://drive.google.com/uc?export=download&id=1upG0EwvgN0BmzaoJ-RyITHwAn_jp6Qbq");

            long time1 = System.currentTimeMillis();
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new InterruptedException();
            }

            input = connection.getInputStream();
            long time2 = System.currentTimeMillis();

            float dl_time = (time2-time1)/1000f;

            this.context.runOnUiThread(() -> {
                output_ui.append(String.format("%f MBps", 4f/dl_time) + "\n\n");
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {

            if (connection != null)
                connection.disconnect();

            this.context.runOnUiThread(() -> {
                output_ui.append("\n------- stopped!");
                ((SpeedTestActivity) context).enableUI();
            });
        }
    }
}
