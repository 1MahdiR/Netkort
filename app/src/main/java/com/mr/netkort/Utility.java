package com.mr.netkort;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utility {

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

    public static Pair<Boolean, String> ping(String host_ip, String host_address, int timeout, boolean verbose) {

        Pair<Boolean, String> pair;

        try {
            @SuppressLint("DefaultLocale")
            Process p = Runtime.getRuntime().exec(String.format("/system/bin/ping -c 1 -W %d %s", timeout, host_ip));

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
                if (verbose) {
                    pair = new Pair<>(true, String.format("ICMP packet received from host %s (%s)", host_address, host_ip));
                } else {
                    pair = new Pair<>(true, String.format("ICMP packet received from (%s)", host_ip));
                }
            } else {
                if (verbose) {
                    pair = new Pair<>(false, String.format("ICMP packet failed from %s", host_address));
                } else {
                    pair = new Pair<>(false, "ICMP packet failed");
                }
            }

            return pair;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }

        if (verbose) {
            pair = new Pair<>(false, String.format("ICMP packet failed from %s", host_address));
        } else {
            pair = new Pair<>(false, "ICMP packet failed");
        }
        return pair;
    }
}
