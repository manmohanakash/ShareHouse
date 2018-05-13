package com.akash.sharehouse.helpers;


public class AppConfig {

	#Change server config below
    public static final String hostName = "ec2-18-188-132-77.us-east-2.compute.amazonaws.com";
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

