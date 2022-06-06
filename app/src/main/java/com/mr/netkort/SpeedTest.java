package com.mr.netkort;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class SpeedTest extends Thread {

    private Activity context;
    private TextView output_ui;
    private Calendar calendar;

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
        InputStream input;
        HttpURLConnection connection = null;

        try {
            this.context.runOnUiThread(() -> {
                output_ui.setText("Testing download throughput\n");
                output_ui.append(Utility.getDateTime(this.calendar) + "\n\nDownload rate: ");
            });

            URL url = new URL("https://drive.google.com/uc?export=download&id=1upG0EwvgN0BmzaoJ-RyITHwAn_jp6Qbq");

            while (!Utility.ping("8.8.8.8", 4)) {
                if (!this.isRunning) { throw new InterruptedException(); }
                this.context.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(context, "Network connection error. Trying again...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 500);
                    toast.show();
                });
                sleep(4000);
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new InterruptedException();
            }

            input = connection.getInputStream();

            long time1 = System.currentTimeMillis();
            long time2;
            long count = 0;

            byte[] data = new byte[4096];
            while (input.read(data) != -1) {
                if (!this.isRunning) { throw new InterruptedException(); }

                count += 4096;
                time2 = System.currentTimeMillis();
                float dl_time_temp = (time2-time1)/1000f;
                float bytes = (float)(count / 1000000);

                this.context.runOnUiThread(() -> {
                    output_ui.setText("Testing download throughput\n");
                    output_ui.append(Utility.getDateTime(this.calendar) + "\n\nDownload rate: ");
                    output_ui.append(String.format("%f MBps", bytes/dl_time_temp));
                });
            }

            time2 = System.currentTimeMillis();

            float dl_time = (time2-time1)/1000f;

            this.context.runOnUiThread(() -> {
                output_ui.setText("Testing download throughput\n");
                output_ui.append(Utility.getDateTime(this.calendar) + "\n\nDownload rate: ");
                output_ui.append(String.format("%f MBps", 4f/dl_time));
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {

            if (connection != null)
                connection.disconnect();

            this.context.runOnUiThread(() -> {
                output_ui.append("\n\n\n------- stopped!");
                ((SpeedTestActivity) context).enableUI();
            });
        }
    }
}
