package com.tny.game.common.utils.md5;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.concurrent.ThreadLocalRandom;

public class MD5 {

    public static String md5(String content) {
        return DigestUtils.md5Hex(content);
    }


    public static String md5(byte [] content) {
        return DigestUtils.md5Hex(content);
    }


    static String values = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"\\/|~!@#$%^&*()[]{};':\",./<>?";

    public static void main(String[] args) {
        int time = 10000000;
        int length = 100;
        for (int i = 0; i < time; i++) {
            StringBuilder word = new StringBuilder(length);
            while (word.length() < length) {
                int index = ThreadLocalRandom.current().nextInt(values.length());
                word.append(values.charAt(index));
            }
            String v = word.toString();
            if (!MD5.md5(v).equals(SignAide.md5Hex(v)))
                System.out.println(v);
        }

    }

}
