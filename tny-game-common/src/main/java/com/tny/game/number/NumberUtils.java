package com.tny.game.number;

import com.tny.game.LogUtils;

/**
 * Created by Kun Yang on 16/2/17.
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    @SuppressWarnings("unchecked")
    public static <N extends Number> N as(Number source, N target) {
        Number value;
        if (target instanceof Integer)
            value = source.intValue();
        else if (target instanceof Long)
            value = source.longValue();
        else if (target instanceof Float)
            value = source.floatValue();
        else if (target instanceof Double)
            value = source.doubleValue();
        else if (target instanceof Short)
            value = source.shortValue();

        else if (target instanceof Byte)
            value = source.byteValue();
        else
            value = source.intValue();
        return (N) value;
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N as(Object source, Class<N> clazz) {
        Number value;
        if (!(source instanceof Number))
            throw new ClassCastException(LogUtils.format("{} {} 不属于 {}", source, source.getClass(), Number.class));
        value = (Number) source;
        if (Integer.class == clazz)
            value = value.intValue();
        else if (Long.class == clazz)
            value = value.longValue();
        else if (Float.class == clazz)
            value = value.floatValue();
        else if (Double.class == clazz)
            value = value.doubleValue();
        else if (Short.class == clazz)
            value = value.shortValue();
        else if (Byte.class == clazz)
            value = value.byteValue();
        else
            value = value.intValue();
        return (N) value;
    }

    public static <N extends Number> N add(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        if (one instanceof Integer)
            return as(one.intValue() + other.intValue(), one);
        if (one instanceof Long)
            return as(one.longValue() + other.longValue(), one);
        if (one instanceof Float)
            return as(one.floatValue() + other.floatValue(), one);
        if (one instanceof Double)
            return as(one.doubleValue() + other.doubleValue(), one);
        if (one instanceof Short)
            return as(one.shortValue() + other.shortValue(), one);
        if (one instanceof Byte)
            return as(one.byteValue() + other.byteValue(), one);
        return as(one.doubleValue() + other.doubleValue(), one);
    }

    public static <N extends Number> N sub(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        if (one instanceof Integer)
            return as(one.intValue() - other.intValue(), one);
        if (one instanceof Long)
            return as(one.longValue() - other.longValue(), one);
        if (one instanceof Float)
            return as(one.floatValue() - other.floatValue(), one);
        if (one instanceof Double)
            return as(one.doubleValue() - other.doubleValue(), one);
        if (one instanceof Short)
            return as(one.shortValue() - other.shortValue(), one);
        if (one instanceof Byte)
            return as(one.byteValue() - other.byteValue(), one);
        return as(one.doubleValue() - other.doubleValue(), one);
    }

    public static <N extends Number> N multiply(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        if (one instanceof Integer)
            return as(one.intValue() * other.intValue(), one);
        if (one instanceof Long)
            return as(one.longValue() * other.longValue(), one);
        if (one instanceof Float)
            return as(one.floatValue() * other.floatValue(), one);
        if (one instanceof Double)
            return as(one.doubleValue() * other.doubleValue(), one);
        if (one instanceof Short)
            return as(one.shortValue() * other.shortValue(), one);
        if (one instanceof Byte)
            return as(one.byteValue() * other.byteValue(), one);
        return as(one.doubleValue() * other.doubleValue(), one);
    }

    public static <N extends Number> N divide(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        if (one instanceof Integer)
            return as(one.intValue() / other.intValue(), one);
        if (one instanceof Long)
            return as(one.longValue() / other.longValue(), one);
        if (one instanceof Float)
            return as(one.floatValue() / other.floatValue(), one);
        if (one instanceof Double)
            return as(one.doubleValue() / other.doubleValue(), one);
        if (one instanceof Short)
            return as(one.shortValue() / other.shortValue(), one);
        if (one instanceof Byte)
            return as(one.byteValue() / other.byteValue(), one);
        return as(one.doubleValue() / other.doubleValue(), one);
    }

    public static <N extends Number> N mod(N one, N other) {
        if (one == null)
            return other;
        if (other == null)
            return one;
        if (one instanceof Integer)
            return as(one.intValue() % other.intValue(), one);
        if (one instanceof Long)
            return as(one.longValue() % other.longValue(), one);
        if (one instanceof Float)
            return as(one.floatValue() % other.floatValue(), one);
        if (one instanceof Double)
            return as(one.doubleValue() % other.doubleValue(), one);
        if (one instanceof Short)
            return as(one.shortValue() % other.shortValue(), one);
        if (one instanceof Byte)
            return as(one.byteValue() % other.byteValue(), one);
        return as(one.doubleValue() % other.doubleValue(), one);
    }

    public static boolean less(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() < other.intValue();
        if (one instanceof Long)
            return one.longValue() < other.longValue();
        if (one instanceof Float)
            return one.floatValue() < other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() < other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() < other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() < other.byteValue();
        return one.doubleValue() < other.doubleValue();
    }


    public static boolean lessEqual(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() <= other.intValue();
        if (one instanceof Long)
            return one.longValue() <= other.longValue();
        if (one instanceof Float)
            return one.floatValue() <= other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() <= other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() <= other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() <= other.byteValue();
        return one.doubleValue() <= other.doubleValue();
    }


    public static boolean greater(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() > other.intValue();
        if (one instanceof Long)
            return one.longValue() > other.longValue();
        if (one instanceof Float)
            return one.floatValue() > other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() > other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() > other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() > other.byteValue();
        return one.doubleValue() > other.doubleValue();
    }

    public static boolean greaterEqual(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() >= other.intValue();
        if (one instanceof Long)
            return one.longValue() >= other.longValue();
        if (one instanceof Float)
            return one.floatValue() >= other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() >= other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() >= other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() >= other.byteValue();
        return one.doubleValue() >= other.doubleValue();
    }


    public static boolean equal(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() == other.intValue();
        if (one instanceof Long)
            return one.longValue() == other.longValue();
        if (one instanceof Float)
            return one.floatValue() == other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() == other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() == other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() == other.byteValue();
        return one.doubleValue() == other.doubleValue();
    }

    public static boolean notEqual(Number one, Number other) {
        return !equal(one, other);
    }

}
