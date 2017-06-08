package com.tny.game.common.reflect;

import com.tny.game.number.NumberUtils;
import org.joda.time.DateTime;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/3/9.
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    public static <T> T ifNull(T object, T defObject) {
        return object == null ? defObject : object;
    }

    public static <T> T ifNull(T object, Supplier<T> defObject) {
        return object == null ? defObject.get() : object;
    }

    public static <T, O> O ifNotNull(T object, Function<T, O> mapper, O defObject) {
        return object == null ? defObject : mapper.apply(object);
    }

    public static <T, O> O ifNotNullElse(T object, Function<T, O> mapper, Supplier<? extends O> supplier) {
        return object == null ? supplier.get() : mapper.apply(object);
    }

    public static <V, R> R ifEquals(V one, V other, R trueValue, R falseValue) {
        if (one.equals(other))
            return trueValue;
        return falseValue;
    }

    public static <V, R> R ifEquals(V one, V other, Supplier<R> trueValue, R falseValue) {
        if (one.equals(other))
            return trueValue.get();
        return falseValue;
    }

    public static <V, R> R ifEquals(V one, V other, R trueValue, Supplier<R> falseValue) {
        if (one.equals(other))
            return trueValue;
        return falseValue.get();
    }

    public static <V, R> R ifEquals(V one, V other, Supplier<R> trueValue, Supplier<R> falseValue) {
        if (one.equals(other))
            return trueValue.get();
        return falseValue.get();
    }

    public static <V, R> R ifEqualsThenNull(V one, V other, R trueValue) {
        if (one.equals(other))
            return null;
        return trueValue;
    }

    public static <V, R> R ifEqualsThenNull(V one, V other, Supplier<R> trueValue) {
        if (one.equals(other))
            return null;
        return trueValue.get();
    }


    public static <T> T self(T object) {
        return object;
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object object, Class<T> clazz) {
        if (Number.class.isAssignableFrom(clazz)) {
            return (T) NumberUtils.as(object, (Class<? extends Number>) clazz);
        }
        if (clazz.isInstance(object))
            return (T) object;
        throw new ClassCastException(object + "is not " + clazz + "instance");
    }

    @SuppressWarnings("unchecked")
    public static <T> T as(Object object) {
        return (T) object;
    }

    public static void main(String[] args) {
        DateTime date = DateTime.now();
        long test = 222L;
        long value = ifNotNullElse(date, DateTime::getMillis, () -> test);
        date = ifNull(date, DateTime::now);
    }

}
