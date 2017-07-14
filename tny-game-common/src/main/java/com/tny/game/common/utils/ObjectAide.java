package com.tny.game.common.utils;

import com.tny.game.common.number.NumberAide;
import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.DateTime;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/3/9.
 */
public class ObjectAide extends ObjectUtils {

    public static <T> T none() {
        return null;
    }

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

    /**
     * 如果 one equals other 返回 one, 否则返回elseValue
     *
     * @param one       值
     * @param other     期望值
     * @param elseValue 其他值
     * @return one equals other 返回 one, 否则返回elseValue
     */
    public static <V> V ifEqualsElse(V one, V other, V elseValue) {
        if (one.equals(other))
            return one;
        return elseValue;
    }

    /**
     * 如果 one equals other 返回 one, 否则返回elseValue.get()
     *
     * @param one       值
     * @param other     期望值
     * @param elseValue 其他值提供者
     * @return one equals other 返回 one, 否则返回elseValue.get()
     */
    public static <V, R extends V> V ifEqualsElse(V one, V other, Supplier<R> elseValue) {
        if (one.equals(other))
            return one;
        return elseValue.get();
    }

    /**
     * 如果 one !equals other 返回 one, 否则返回elseValue
     *
     * @param one       值
     * @param other     期望值
     * @param elseValue 其他值
     * @return one !equals other 返回 one, 否则返回elseValue
     */
    public static <V> V ifNotEqualsElse(V one, V other, V elseValue) {
        if (!one.equals(other))
            return one;
        return elseValue;
    }


    /**
     * 如果 one !equals other 返回 one, 否则返回elseValue.get()
     *
     * @param one       值
     * @param other     期望值
     * @param elseValue 其他值提供者
     * @return one !equals other 返回 one, 否则返回elseValue.get()
     */
    public static <V, R extends V> V ifNotEqualsElse(V one, V other, Supplier<R> elseValue) {
        if (!one.equals(other))
            return one;
        return elseValue.get();
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
            return (T) NumberAide.as(object, (Class<? extends Number>) clazz);
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
