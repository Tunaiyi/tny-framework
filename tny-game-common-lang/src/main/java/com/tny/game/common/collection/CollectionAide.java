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

package com.tny.game.common.collection;

import com.tny.game.common.utils.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;

public class CollectionAide {

    public final static char[] CHAR_EMPTY_ARRAY = new char[0];

    public final static byte[] BYTE_EMPTY_ARRAY = new byte[0];

    public final static int[] INT_EMPTY_ARRAY = new int[0];

    public final static short[] SHORT_EMPTY_ARRAY = new short[0];

    public final static long[] LONG_EMPTY_ARRAY = new long[0];

    public final static float[] FLOAT_EMPTY_ARRAY = new float[0];

    public final static double[] DOUBLE_EMPTY_ARRAY = new double[0];

    public final static boolean[] BOOLEAN_EMPTY_ARRAY = new boolean[0];

    private final static HashMap<Class<?>, Object> EMPTY_ARRAY_MAP = new HashMap<>();

    static {
        EMPTY_ARRAY_MAP.put(char[].class, CHAR_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(char.class, CHAR_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(byte[].class, BYTE_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(byte.class, BYTE_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(int[].class, INT_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(int.class, INT_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(short[].class, SHORT_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(short.class, SHORT_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(float[].class, FLOAT_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(float.class, FLOAT_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(double[].class, DOUBLE_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(double.class, DOUBLE_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(boolean[].class, BOOLEAN_EMPTY_ARRAY);
        EMPTY_ARRAY_MAP.put(boolean.class, BOOLEAN_EMPTY_ARRAY);
    }

    public final static Object[] EMPTY_ARRAYS = new Object[]{
            CHAR_EMPTY_ARRAY, BYTE_EMPTY_ARRAY, INT_EMPTY_ARRAY, SHORT_EMPTY_ARRAY, LONG_EMPTY_ARRAY,
            FLOAT_EMPTY_ARRAY, DOUBLE_EMPTY_ARRAY, BOOLEAN_EMPTY_ARRAY
    };

    public static Object toArray(Collection<?> collection, Class<?> clazz) {
        Object message = Array.newInstance(clazz, collection.size());
        int index = 0;
        for (Object value : collection) {
            Array.set(message, index++, value);
        }
        return message;
    }

    public static <K, T> T loadOrPut(Map<K, T> map, K key, Supplier<T> creator) {
        T value = map.get(key);
        if (value == null) {
            value = creator.get();
            value = ObjectAide.ifNull(
                    map.putIfAbsent(key, value), value);
        }
        return value;
    }

    public static Map<String, Object> attributes2Map(Object... objects) {
        return attributes2Map(null, objects);
    }

    public static Map<String, Object> attributes2Map(Map<String, Object> src, Object... objects) {
        if (objects.length == 0) {
            if (src == null) {
                return Collections.emptyMap();
            }
            return src;
        }
        Object key = null;
        if (src == null) {
            src = new HashMap<>();
        }
        for (Object object : objects) {
            if (key == null) {
                key = object;
            } else {
                src.put(key.toString(), object);
                key = null;
            }
        }
        return src;
    }

    public static Map<Object, Object> arrays2Map(Map<Object, Object> src, Object... objects) {
        if (objects.length == 0) {
            return Collections.emptyMap();
        }
        Object key = null;
        if (src == null) {
            src = new HashMap<>();
        }
        for (Object object : objects) {
            if (key == null) {
                key = object;
            } else {
                src.put(key, object);
                key = null;
            }
        }
        return src;
    }

    public static Map<Object, Object> arrays2Map(Object... objects) {
        return arrays2Map(null, objects);
    }

    public static <T> Set<T> union(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new HashSet<>(setA);
        tmp.addAll(setB);
        return tmp;
    }

    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new HashSet<>(setA);
        tmp.retainAll(setB);
        return tmp;
    }

    public static <T> SortedSet<T> intersection(SortedSet<T> setA, SortedSet<T> setB) {
        SortedSet<T> tmp = new TreeSet<>(setA);
        tmp.retainAll(setB);
        return tmp;
    }

    public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
        Set<T> tmp = new HashSet<>(setA);
        tmp.removeAll(setB);
        return tmp;
    }

    public static <T> Set<T> symDifference(Set<T> setA, Set<T> setB) {
        Set<T> tmpA;
        Set<T> tmpB;
        tmpA = union(setA, setB);
        tmpB = intersection(setA, setB);
        return difference(tmpA, tmpB);
    }

    public static <T> boolean isSubset(Set<T> setA, Set<T> setB) {
        return setB.containsAll(setA);
    }

    public static <T> boolean isSuperset(Set<T> setA, Set<T> setB) {
        return setA.containsAll(setB);
    }

    public static <T> Collection<T> union(Collection<T> setA, Collection<T> setB) {
        Collection<T> tmp = new ArrayList<>(setA);
        tmp.addAll(setB);
        return tmp;
    }

    public static <T> Collection<T> intersection(Collection<T> setA, Collection<T> setB) {
        Collection<T> tmp = new ArrayList<>(setA);
        tmp.retainAll(setB);
        return tmp;
    }

    public static <T> Collection<T> difference(Collection<T> setA, Collection<T> setB) {
        Collection<T> tmp = new ArrayList<>(setA);
        tmp.removeAll(setB);
        return tmp;
    }

    public static <T> Collection<T> symDifference(Collection<T> setA, Collection<T> setB) {
        Collection<T> tmpA;
        Collection<T> tmpB;
        tmpA = union(setA, setB);
        tmpB = intersection(setA, setB);
        return difference(tmpA, tmpB);
    }

    public static <T> boolean isSubset(Collection<T> setA, Collection<T> setB) {
        return setB.containsAll(setA);
    }

    public static <T> boolean isSuperset(Collection<T> setA, Collection<T> setB) {
        return setA.containsAll(setB);
    }

    public static <T> List<T> union(Collection<T> setA, List<T> setB) {
        List<T> tmp = new ArrayList<>(setA);
        tmp.addAll(setB);
        return tmp;
    }

    public static <T> List<T> intersection(List<T> setA, List<T> setB) {
        List<T> tmp = new ArrayList<>(setA);
        tmp.retainAll(setB);
        return tmp;
    }

    public static <T> List<T> difference(List<T> setA, List<T> setB) {
        List<T> tmp = new ArrayList<>(setA);
        tmp.removeAll(setB);
        return tmp;
    }

    public static <T> List<T> symDifference(List<T> setA, List<T> setB) {
        List<T> tmpA;
        List<T> tmpB;
        tmpA = union(setA, setB);
        tmpB = intersection(setA, setB);
        return difference(tmpA, tmpB);
    }

    public static <T> boolean isSubset(List<T> setA, List<T> setB) {
        return setB.containsAll(setA);
    }

    public static <T> boolean isSuperset(List<T> setA, List<T> setB) {
        return setA.containsAll(setB);
    }

    public static <T, C extends Collection<T>> C ifEmpty(C collection, C defObject) {
        return isEmpty(collection) ? defObject : collection;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static Map<String, String> toStringMap(String... pairs) {
        Map<String, String> map = new HashMap<>();
        if (pairs.length > 0) {
            Asserts.checkArgument(pairs.length % 2 == 0, "pairs 数量非偶数");
            for (int index = 0; index < pairs.length; index = index + 2) {
                map.put(pairs[index], pairs[index + 1]);
            }
        }
        return map;
    }

}
