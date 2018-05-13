package com.akash.sharehouse.helpers;
import java.math.BigInteger;
import java.security.MessageDigest;

public class hashing {

    public static String hashString(String str) {

        String hashedStr = str;

        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(str.getBytes());
            hashedStr = String.format("%0" + (d.length * 2) + "X", new BigInteger(1, d));
        }
        catch(Exception e){}

        return hashedStr;
    }
}
