package com.tny.game.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Easily convert byte with short, int, long, float, double.
 *
 * @author elliot
 */
public class ByteUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(ByteUtil.class);

    /**
     * Convert long to byte array.
     *
     * @param number The data to be converted.
     * @return 8-digit byte array.
     */
    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return b;
    }

    /**
     * Convert byte array to long.
     *
     * @param b The byte array to be converted.
     * @return
     */
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * Convert int to byte array.
     *
     * @param number The int value to be converted.
     * @return
     */
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = Long.valueOf(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return b;
    }

    /**
     * Convert byte array to int.
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    /**
     * Convert short to byte array.
     *
     * @param number
     * @return
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = Integer.valueOf(temp & 0xff).byteValue();
            temp = temp >> 8;
        }
        return b;
    }

    /**
     * Convert byte array to short.
     *
     * @param b
     * @return
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * Convert double to byte array.
     *
     * @param d
     * @return
     */
    public static byte[] doubleToByte(double d) {
        return longToByte(Double.doubleToLongBits(d));
    }

    /**
     * Convert byte array to double.
     *
     * @param b
     * @return
     */
    public static double byteToDouble(byte[] b) {
        return Double.longBitsToDouble(byteToLong(b));
    }

    /**
     * Convert float to byte array.
     *
     * @param f
     * @return
     */
    public static byte[] floatToByte(float f) {
        return intToByte(Float.floatToIntBits(f));
    }

    /**
     * Convert byte array to float.
     */
    public static float byteToFloat(byte[] b) {
        return Float.intBitsToFloat(byteToInt(b));
    }

    /**
     * Convert byte to unsigned int.
     *
     * @param b
     * @return
     */
    public static int byteToUnsignedInt(byte b) {
        return b & 0xfff;
    }

}