package com.tny.game.common.math;


import com.google.common.collect.Range;

import java.util.*;

/**
 * Created by Kun Yang on 2017/5/30.
 */
public class RangeAide {

    public static Range<Byte> range(Byte min, Byte max) {
        return Range.closed(min, max);
    }

    public static Range<Short> range(Short min, Short max) {
        return Range.closed(min, max);
    }

    public static Range<Integer> range(Integer min, Integer max) {
        return Range.closed(min, max);
    }

    public static Range<Long> range(Long min, Long max) {
        return Range.closed(min, max);
    }

    public static Range<Float> range(Float min, Float max) {
        return Range.closed(min, max);
    }

    public static Range<Double> range(Double min, Double max) {
        return Range.closed(min, max);
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

