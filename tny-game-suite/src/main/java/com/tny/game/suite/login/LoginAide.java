package com.tny.game.suite.login;

import com.tny.game.common.utils.digest.md5.*;
import com.tny.game.suite.utils.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map.Entry;
import java.util.SortedMap;

public class LoginAide {

    public static boolean checkWebSign(String serverType, SortedMap<String, String> param) {
        String timeout = param.get("timeout");
        long remainTime = NumberUtils.toLong(timeout) - System.currentTimeMillis();
        if (timeout == null || !NumberUtils.isDigits(timeout) || remainTime > 180000 || remainTime < 0)
            return false;
        String checkSign = param.get("sign");
        if (checkSign == null)
            return false;
        String sign = webSign(serverType, param);
        return sign.equals(checkSign);
    }

    public static String webSign(String serverType, SortedMap<String, String> param) {
        param.put("timeout", String.valueOf(System.currentTimeMillis() + 60000 * 3));
        StringBuilder builder = new StringBuilder(128);
        for (Entry<String, String> entry : param.entrySet()) {
            if (entry.getKey().equals("sign"))
                continue;
            builder.append(entry.getKey()).append("=")
                   .append(entry.getValue());
        }
        String truePWD = Configs.AUTH_CONFIG.getStr(Configs.createAuthKey(serverType), "");
        return MD5.md5(MD5.md5(builder.toString()) + truePWD);
    }

    public static String ticket2MD5(GameTicket ticket, String key) {
        String ticketStr = String.valueOf(ticket.getTokenId()) +
                           ticket.getOpenId() +
                           ticket.getBindId() +
                           ticket.getOpenKey() +
                           ticket.getAccountTag() +
                           ticket.getServer() +
                           ticket.getTime() +
                           ticket.getPf() +
                           ticket.getZone() +
                           ticket.getEntry() +
                           ticket.getDevice() +
                           ticket.getDeviceId() +
                           ticket.isInterior() +
                           key;
        return MD5.md5(ticketStr);
    }

    // public static void setTicketMD5(GameTicket ticket, String key) {
    //     ticket.setSecret(ticket2MD5(ticket, key));
    // }

    public static String ticket2MD5(ServerTicket ticket, String key) {
        return MD5.md5(ticket.getServerType() +
                       ticket.getServerId() +
                       ticket.getTime() +
                       ticket.isConfirm() +
                       key);
    }

}
