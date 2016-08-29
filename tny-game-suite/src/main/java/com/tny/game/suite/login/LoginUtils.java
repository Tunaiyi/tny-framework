package com.tny.game.suite.login;

import com.tny.game.common.utils.md5.MD5;
import com.tny.game.suite.utils.Configs;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map.Entry;
import java.util.SortedMap;

public class LoginUtils {

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
        StringBuffer ticketBuffer = new StringBuffer(100);
        ticketBuffer.append(ticket.getTokenID());
        ticketBuffer.append(ticket.getOpenID());
        ticketBuffer.append(ticket.getOpenKey());
        ticketBuffer.append(ticket.getServer());
        ticketBuffer.append(ticket.getTime());
        ticketBuffer.append(ticket.getDevice());
        ticketBuffer.append(ticket.getDeviceID());
        ticketBuffer.append(ticket.getPf());
        ticketBuffer.append(ticket.getAccount());
        ticketBuffer.append(ticket.isInterior());
        ticketBuffer.append(key);
        return MD5.md5(ticketBuffer.toString());
    }

    public static void setTicketMD5(GameTicket ticket, String key) {
        ticket.setSecret(ticket2MD5(ticket, key));
    }

    public static String ticket2MD5(ServeTicket ticket, String key) {
        StringBuffer ticketBuffer = new StringBuffer(100);
        ticketBuffer.append(ticket.getServerType());
        ticketBuffer.append(key);
        ticketBuffer.append(ticket.getServerID());
        ticketBuffer.append(ticket.getTime());
        ticketBuffer.append(key);
        return MD5.md5(ticketBuffer.toString());
    }

}
