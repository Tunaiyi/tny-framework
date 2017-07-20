package com.tny.game.suite.login;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.stream.Stream;

public class AccountUtils {

    private static final String SEPARATOR = ".";

    public static final int PF_INDEX = 0;
    public static final int SERVER_ID_INDEX = 1;
    public static final int OPEN_ID_INDEX = 2;

    public static String openID2Account(String accountTag, int serverID, String openid) {
        return accountTag + SEPARATOR + serverID + SEPARATOR + openid;
    }

    public static Object[] breakAccount(String account) {
        String keys[] = StringUtils.split(account, SEPARATOR);
        if (keys == null || keys.length != 3 || !NumberUtils.isDigits(keys[SERVER_ID_INDEX]))
            return null;
        Object[] data = new Object[3];
        data[PF_INDEX] = keys[PF_INDEX];
        data[SERVER_ID_INDEX] = NumberUtils.toInt(keys[SERVER_ID_INDEX]);
        data[OPEN_ID_INDEX] = keys[OPEN_ID_INDEX];
        return data;
    }

    public static String account2Tag(String account) {
        return StringUtils.split(account, SEPARATOR)[PF_INDEX];
    }

    public static String account2OpenID(String account) {
        String[] accWords = StringUtils.split(account, SEPARATOR);
        if (accWords.length == 3)
            return accWords[OPEN_ID_INDEX];
        else {
            StringBuilder builder = new StringBuilder();
            Stream.of(accWords)
                    .skip(2) // 跳过 PF & SERVER_ID
                    .forEach(builder::append);
            return builder.toString();
        }
    }

    /**
     * 根据账号解释出服务器号，出错就返回-1
     *
     * @param account
     * @return
     */
    public static int account2SID(String account) {
        return NumberUtils.toInt(StringUtils.split(account, SEPARATOR)[SERVER_ID_INDEX], -1);
    }

}
