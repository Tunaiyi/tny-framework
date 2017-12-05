package com.tny.game.suite.base.md5;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Kun Yang on 2017/1/16.
 */
public class SignAide {


    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static final String MD5 = "MD5";

    public static byte[] getBytes(String str, Charset charset) {
        return str.getBytes(charset);
    }

    public static byte[] getBytes(String str) {
        return str.getBytes(UTF_8);
    }

    public static byte[] md5Bytes(byte[] data) {
        try {
            return MessageDigest.getInstance(MD5).digest(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] md5Bytes(String data) {
        return md5Bytes(getBytes(data));
    }

    public static String md5Hex(byte[] data) {
        return bytes2Hex(md5Bytes(data));
    }

    public static String md5Hex(String str) {
        return bytes2Hex(md5Bytes(getBytes(str)));
    }

    public static String bytes2Hex(byte[] data) {
        return bytes2Hex(data, true);
    }

    public static String bytes2Hex(byte[] data, final boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER));
    }

    private static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

}
