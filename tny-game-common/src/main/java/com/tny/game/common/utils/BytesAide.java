package com.tny.game.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.*;

/**
 * Easily convert byte with short, int, long, float, double.
 *
 * @author elliot
 */
public class BytesAide {

    public static final Logger LOGGER = LoggerFactory.getLogger(BytesAide.class);

    public static long bytes2Long(byte[] data, int startAt) {
        return (((long) data[startAt] & 0xff))
               | (((long) data[startAt + 1] & 0xff) << 8)
               | (((long) data[startAt + 2] & 0xff) << 16)
               | (((long) data[startAt + 3] & 0xff) << 24)
               | (((long) data[startAt + 4] & 0xff) << 32)
               | (((long) data[startAt + 5] & 0xff) << 40)
               | (((long) data[startAt + 6] & 0xff) << 48)
               | (((long) data[startAt + 7] & 0xff) << 56);
    }

    public static byte[] long2Bytes(long value, byte[] target, int startAt) {
        target[startAt] = ((byte) (value & 0xFF));
        target[startAt + 1] = ((byte) (value >> 8 & 0xFF));
        target[startAt + 2] = ((byte) (value >> 16 & 0xFF));
        target[startAt + 3] = ((byte) (value >> 24 & 0xFF));
        target[startAt + 4] = ((byte) (value >> 32 & 0xFF));
        target[startAt + 5] = ((byte) (value >> 40 & 0xFF));
        target[startAt + 6] = ((byte) (value >> 48 & 0xFF));
        target[startAt + 7] = ((byte) (value >> 56 & 0xFF));
        return target;
    }

    public static int bytes2Int(byte[] data, int startAt) {
        return (((int) data[startAt] & 0xff))
               | (((int) data[startAt + 1] & 0xff) << 8)
               | (((int) data[startAt + 2] & 0xff) << 16)
               | (((int) data[startAt + 3] & 0xff) << 24);
    }

    public static byte[] int2Bytes(int value, byte[] target, int startAt) {
        target[startAt] = ((byte) (value & 0xFF));
        target[startAt + 1] = ((byte) (value >> 8 & 0xFF));
        target[startAt + 2] = ((byte) (value >> 16 & 0xFF));
        target[startAt + 3] = ((byte) (value >> 24 & 0xFF));
        return target;
    }

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
        byte[] data = new byte[4];
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

    public static String toBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public static String toBinaryString(byte[]... bytesArrays) {
        return toBinaryString("", bytesArrays);
    }

    public static String toBinaryString(String separator, byte[]... bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte[] binary : bytes)
            appendBinaryString(builder, separator, binary);
        return builder.toString();
    }

    private static void appendBinaryString(StringBuilder bodyBinary, String separator, byte[] bytes) {
        int index = bodyBinary.length() + 7;
        for (byte data : bytes) {
            bodyBinary.append("00000000");
            for (int bit = 0; bit < 8; bit++) {
                if (((data >> bit) & 1) > 0) {
                    bodyBinary.setCharAt(index - bit, '1');
                }
            }
            int separatorLength = separator.length();
            if (separatorLength > 0)
                bodyBinary.append(separator);
            index += (separatorLength + 8);
        }
    }

    public static String toHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static String toHexString(byte[]... bytesArrays) {
        StringBuilder builder = new StringBuilder();
        for (byte[] bytes : bytesArrays)
            builder.append(Hex.encodeHex(bytes));
        return builder.toString();
    }

    public static void main(String[] args) {
        byte[] message = {Byte.MIN_VALUE, Byte.MIN_VALUE / 2, 0, Byte.MAX_VALUE / 2, Byte.MAX_VALUE};
        System.out.println(Hex.encodeHex(new byte[]{0}));
        for (byte b : message)
            System.out.print(toBinaryString(b) + " ");
        System.out.println();
        System.out.println(toHexString(message));
        System.out.println(toBinaryString(" ", message, message));
        System.out.println(toHexString(message, message));
    }

    public static byte[] xor(byte[] data, byte[]... keyBytes) {
        for (int i = 0; i < data.length; i++) {
            for (byte[] keys : keyBytes) {
                data[i] = (byte) (data[i] ^ keys[i % keys.length]);
            }
        }
        return data;
    }

}