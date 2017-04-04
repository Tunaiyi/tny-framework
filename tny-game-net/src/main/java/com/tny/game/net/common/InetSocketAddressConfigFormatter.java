package com.tny.game.net.common;

import com.tny.game.common.config.ConfigFormatter;
import com.tny.game.number.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kun Yang on 2017/3/29.
 */
public class InetSocketAddressConfigFormatter implements ConfigFormatter {

    public static final InetSocketAddressConfigFormatter FORMATTER = new InetSocketAddressConfigFormatter();

    @Override
    public boolean isKey(String key) {
        return key.endsWith(".ips") || key.endsWith(".ip") || key.endsWith(".bind") || key.endsWith(".listener") || key.endsWith(".address") || key.endsWith(".addresses");
    }

    @Override
    public Object formatObject(String value) {
        String[] ips = StringUtils.split(value, "|");
        List<InetSocketAddress> addresses = new ArrayList<>();
        for (String ipStr : ips) {
            String[] ip = StringUtils.split(ipStr, ":");
            addresses.add(new InetSocketAddress(ip[0], NumberUtils.toInt(ip[1])));
        }
        return addresses;
    }

}
