package com.tny.game.common.formula;


import com.google.common.collect.Range;

/**
 * Created by Kun Yang on 2017/5/30.
 */
public class MvelEx {

    public static int[] ints(int... value) {
        return value;
    }

    public static long[] longs(long... value) {
        return value;
    }

    public static double[] doubles(double... value) {
        return value;
    }

    public static float[] floas(float... value) {
        return value;
    }

    public static short[] shorts(short... value) {
        return value;
    }

    public static String[] strs(String... value) {
        return value;
    }

    public static <T> T[] objects(T... value) {
        return value;
    }

    public static <T extends Comparable<T>> Range<T> range(T min, T max) {
        return Range.closed(min, max);
    }

    public static <T extends Comparable<T>> boolean overlapped(Range<T> one, Range<T> other) {
        if (other == null) {
            return false;
        }
        return other.contains(one.lowerEndpoint())
                || other.contains(one.upperEndpoint())
                || one.contains(other.lowerEndpoint());
    }

}
