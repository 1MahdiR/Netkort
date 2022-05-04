package com.mr.netkort;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static String getHostIp(String host_address) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            return InetAddress.getByName(host_address).getHostAddress();

        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static Pair<Traceroute.Stat, String> parse_ping_output(String output, boolean ping_result, String host_ip) {

        String ip_pattern = "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
        Pattern p = Pattern.compile(ip_pattern);

        ArrayList<String> all_matches = new ArrayList<>();
        Matcher m = p.matcher(output);

        while (m.find()) {
            all_matches.add(m.group());
        }

        if (all_matches.size() < 4) {
            return new Pair<>(Traceroute.Stat.NO_RESPONSE, "");
        }

        String host_reached = all_matches.get(2);

        if (all_matches.get(2) != host_ip && !ping_result) {
            return new Pair<>(Traceroute.Stat.TTL_EXCEEDED, host_reached);
        }

        if (ping_result) {
            return new Pair<>(Traceroute.Stat.REACHED, host_ip);
        }

        return new Pair<>(Traceroute.Stat.ERROR, "");
    }

    public static Boolean ping(String host_ip, int timeout) {

        try {
            @SuppressLint("DefaultLocale")
            Process p = Runtime.getRuntime().exec(String.format("/system/bin/ping -c 1 -t 30 -W %d %s", timeout, host_ip));

            // For reading ping's output
            /*BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }*/

            int result = p.waitFor();

            return result == 0;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }

        return false;
    }

    public static Pair<Traceroute.Stat, String> traceroute_ping(String host_ip, int timeout, int ttl) {

        try {
            @SuppressLint("DefaultLocale")
            Process p = Runtime.getRuntime().exec(String.format("/system/bin/ping -c 1 -t %d -W %d %s", ttl, timeout, host_ip));

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            String output = "";
            while ((line = bufferedReader.readLine()) != null) {
                output += line;
            }
            //System.out.println(output);

            int result = p.waitFor();

            //System.out.println(pair.first);
            return parse_ping_output(output, result == 0, host_ip);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        }

        return new Pair<>(Traceroute.Stat.ERROR, "");
    }
}
