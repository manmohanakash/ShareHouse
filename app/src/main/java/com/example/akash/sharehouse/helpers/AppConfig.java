package com.example.akash.sharehouse.helpers;


public class AppConfig {


    public static final String hostName = "10.219.68.3";
    public static final int port = 8080;

    public AppConfig() {
    }

    public static String getHostName() {
        return hostName;
    }

    public static int getPort() {
        return port;
    }
}
