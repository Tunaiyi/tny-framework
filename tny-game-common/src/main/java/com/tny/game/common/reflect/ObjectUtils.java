package com.tny.game.common.reflect;

import com.tny.game.number.NumberUtils;

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

}
