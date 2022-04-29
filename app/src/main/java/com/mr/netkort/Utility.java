package com.mr.netkort;

import android.annotation.SuppressLint;
import android.os.StrictMode;

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

    public static Boolean ping(String host_ip, int timeout, int ttl) {

        try {
            @SuppressLint("DefaultLocale")
            Process p = Runtime.getRuntime().exec(String.format("/system/bin/ping -c 1 -t %d -W %d %s", ttl, timeout, host_ip));

            // For reading ping's output
            /*
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            */

            int result = p.waitFor();

            return result == 0;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }

        return false;
    }

    public static Boolean ping(String host_ip, int timeout) {

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

            return result == 0;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }

        return false;
    }
}
