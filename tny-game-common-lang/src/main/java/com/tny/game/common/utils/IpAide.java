package com.tny.game.common.utils;

import java.net.*;
import java.util.regex.Pattern;

/**
 * Created by Kun Yang on 2017/9/7.
 */
public interface IpAide {

    Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    /**
     * @param hostName 域名
     * @return 获取hostName的IP
     */
    static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    static boolean isLocalHost(String host) {
        return host != null
                && (LOCAL_IP_PATTERN.matcher(host).matches()
                || host.equalsIgnoreCase("localhost"));
    }

}
