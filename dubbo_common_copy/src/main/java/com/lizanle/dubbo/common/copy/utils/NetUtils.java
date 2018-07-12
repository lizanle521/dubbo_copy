package com.lizanle.dubbo.common.copy.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
    public static final String LOCALHOST = "127.0.0.1";

    private static volatile InetAddress INETADDRESS;

    public static String getIpByHost(String hostName){
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    public static String getLogHost(){
        return NetUtils.INETADDRESS == null ? LOCALHOST : INETADDRESS.getHostAddress();
    }
}
