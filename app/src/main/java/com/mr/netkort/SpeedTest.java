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
        long total_count = 0;
        long average_time_1 = 0;
        long average_time_2 = 0;
        float max_rate = 0f;

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
            average_time_1 = System.currentTimeMillis();

            while (true) {
                if (!this.isRunning) { throw new InterruptedException(); }
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
                    total_count += 4096;
                    time2 = System.currentTimeMillis();
                    float dl_time_temp = (time2-time1)/1000f;
                    float bytes = (float)(count / 1000000);
                    float rate = bytes/dl_time_temp;

                    this.context.runOnUiThread(() -> {
                        output_ui.setText("Testing download throughput\n");
                        output_ui.append(Utility.getDateTime(this.calendar) + "\n\nDownload rate: ");
                        output_ui.append(String.format("%.3f MBps", rate));
                    });

                    if (rate > max_rate)
                        max_rate = rate;
                }
            }

        } catch (IOException | InterruptedException e) {
            average_time_2 = System.currentTimeMillis();
            e.printStackTrace();
        } finally {

            if (connection != null)
                connection.disconnect();

            this.context.runOnUiThread(() -> {
                output_ui.append("\n\n\n------- stopped!");
                ((SpeedTestActivity) context).enableUI();
            });

            if (total_count > 0) {
                float average_dl_time = (average_time_2 - average_time_1)/1000f;
                float bytes = (float)(total_count / 1000000);
                float average_rate = bytes/average_dl_time;
                float max_rate_temp = max_rate;

                this.context.runOnUiThread(() -> {
                    output_ui.append(String.format("\n\n\nMaximum download rate: %.3f MBps\n", max_rate_temp));
                    output_ui.append(String.format("Average download rate: %.3f MBpx", average_rate));
                    ((SpeedTestActivity) context).enableUI();
                });
            }
        }
    }
}
