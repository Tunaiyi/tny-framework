package com.tny.game.suite.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Easily convert byte with short, int, long, float, double.
 *
 * @author elliot
 */
public class BytesAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(BytesAide.class);

    public static long bytes2Long(byte[] data) {
        return (((long) data[0] & 0xff))
                | (((long) data[1] & 0xff) << 8)
                | (((long) data[2] & 0xff) << 16)
                | (((long) data[3] & 0xff) << 24)
                | (((long) data[4] & 0xff) << 32)
                | (((long) data[5] & 0xff) << 40)
                | (((long) data[6] & 0xff) << 48)
                | (((long) data[7] & 0xff) << 56);
    }

    public static byte[] long2Bytes(long value) {
        byte[] data = new byte[8];
        data[0] = ((byte) (value & 0xFF));
        data[1] = ((byte) (value >> 8 & 0xFF));
        data[2] = ((byte) (value >> 16 & 0xFF));
        data[3] = ((byte) (value >> 24 & 0xFF));
        data[4] = ((byte) (value >> 32 & 0xFF));
        data[5] = ((byte) (value >> 40 & 0xFF));
        data[6] = ((byte) (value >> 48 & 0xFF));
        data[7] = ((byte) (value >> 56 & 0xFF));
        return data;
    }

    public static int bytes2Int(byte[] data) {
        return (((int) data[0] & 0xff))
                | (((int) data[1] & 0xff) << 8)
                | (((int) data[2] & 0xff) << 16)
                | (((int) data[3] & 0xff) << 24);
    }

    public static byte[] int2Bytes(int value) {
        byte[] data = new byte[8];
        data[0] = ((byte) (value & 0xFF));
        data[1] = ((byte) (value >> 8 & 0xFF));
        data[2] = ((byte) (value >> 16 & 0xFF));
        data[3] = ((byte) (value >> 24 & 0xFF));
        return data;
    }

    /**
     * Convert short to byte array.
     *
     * @param number
     * @return
     */
    public static byte[] short2Bytes(short number) {
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
    public static short bytes2Short(byte[] b) {
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
    public static byte[] double2Bytes(double d) {
        return long2Bytes(Double.doubleToLongBits(d));
    }

    /**
     * Convert byte array to double.
     *
     * @param b
     * @return
     */
    public static double bytes2Double(byte[] b) {
        return Double.longBitsToDouble(bytes2Long(b));
    }

    /**
     * Convert float to byte array.
     *
     * @param f
     * @return
     */
    public static byte[] float2Bytes(float f) {
        return int2Bytes(Float.floatToIntBits(f));
    }

    /**
     * Convert byte array to float.
     */
    public static float bytes2Float(byte[] b) {
        return Float.intBitsToFloat(bytes2Int(b));
    }

    /**
     * Convert byte to unsigned int.
     *
     * @param b
     * @return
     */
    public static int bytes2UnsignedInt(byte b) {
        return b & 0xfff;
    }

}