package com.tny.game.number;

/**
 * Created by Kun Yang on 16/2/17.
 */
public class NumberUtils extends org.apache.commons.lang3.math.NumberUtils {

    @SuppressWarnings("unchecked")
    public static <N extends Number> N changeAs(Number source, N target) {
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

    public static Number add(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() + other.intValue();
        if (one instanceof Long)
            return one.longValue() + other.longValue();
        if (one instanceof Float)
            return one.floatValue() + other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() + other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() + other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() + other.byteValue();
        return one.doubleValue() + other.doubleValue();
    }

    public static Number sub(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() - other.intValue();
        if (one instanceof Long)
            return one.longValue() - other.longValue();
        if (one instanceof Float)
            return one.floatValue() - other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() - other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() - other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() - other.byteValue();
        return one.doubleValue() - other.doubleValue();
    }

    public static Number multiply(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() * other.intValue();
        if (one instanceof Long)
            return one.longValue() * other.longValue();
        if (one instanceof Float)
            return one.floatValue() * other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() * other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() * other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() * other.byteValue();
        return one.doubleValue() * other.doubleValue();
    }

    public static Number divide(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() / other.intValue();
        if (one instanceof Long)
            return one.longValue() / other.longValue();
        if (one instanceof Float)
            return one.floatValue() / other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() / other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() / other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() / other.byteValue();
        return one.doubleValue() / other.doubleValue();
    }

    public static Number mod(Number one, Number other) {
        if (one instanceof Integer)
            return one.intValue() % other.intValue();
        if (one instanceof Long)
            return one.longValue() % other.longValue();
        if (one instanceof Float)
            return one.floatValue() % other.floatValue();
        if (one instanceof Double)
            return one.doubleValue() % other.doubleValue();
        if (one instanceof Short)
            return one.shortValue() % other.shortValue();
        if (one instanceof Byte)
            return one.byteValue() % other.byteValue();
        return one.doubleValue() % other.doubleValue();
    }

}
