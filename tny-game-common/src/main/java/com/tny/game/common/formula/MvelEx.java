package com.tny.game.common.formula;


import com.google.common.collect.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    public static <T> Set<T> toSet(T... values) {
        Set<T> set = new HashSet<>();
        set.addAll(Arrays.asList(values));
        return set;
    }

    public static <T> List<T> toList(T... values) {
        List<T> list = new ArrayList<>();
        list.addAll(Arrays.asList(values));
        return list;
    }

}

