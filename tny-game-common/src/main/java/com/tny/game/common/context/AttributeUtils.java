package com.tny.game.common.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AttributeUtils {

    private static ConcurrentMap<Object, AttrKey<?>> KEY_MAP = new ConcurrentHashMap<Object, AttrKey<?>>();

    private static class DefauleAttributeKey<T> implements AttrKey<T> {
    }

    @SuppressWarnings("unchecked")
    public static <T> AttrKey<T> key(Class<?> clazz, String object) {
        String key = clazz + object;
        AttrKey<T> value = (AttrKey<T>) KEY_MAP.get(key);
        if (value == null) {
            value = new DefauleAttributeKey<T>();
            AttrKey<?> old = KEY_MAP.putIfAbsent(object, value);
            if (old != null)
                value = (AttrKey<T>) old;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> AttrKey<T> key(Object object) {
        AttrKey<T> value = (AttrKey<T>) KEY_MAP.get(object);
        if (value == null) {
            value = new DefauleAttributeKey<T>();
            AttrKey<?> old = KEY_MAP.putIfAbsent(object, value);
            if (old != null)
                value = (AttrKey<T>) old;
        }
        return value;
    }

}
