package com.tny.game.common.collection;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-25 02:23
 */
public class ArrayAide {

    public static int[] intArray(int... value) {
        return value;
    }

    public static long[] longArray(long... value) {
        return value;
    }

    public static double[] doubleArray(double... value) {
        return value;
    }

    public static float[] floatArray(float... value) {
        return value;
    }

    public static short[] shortArray(short... value) {
        return value;
    }

    public static String[] stringArray(String... value) {
        return value;
    }

    @SafeVarargs
    public static <T> T[] objectArray(T... value) {
        return value;
    }

}
