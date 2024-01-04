/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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

    @SafeVarargs
    public static <T> Set<T> toSet(T... values) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, values);
        return set;
    }

    @SafeVarargs
    public static <T> List<T> toList(T... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

}

