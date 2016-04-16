package com.tny.game.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class AttributeUtils {

    private static ConcurrentMap<Object, AttrKey<?>> KEY_MAP = new ConcurrentHashMap<>();

    private static class DefaultAttributeKey<T> implements AttrKey<T> {

        private String name;

        DefaultAttributeKey(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> AttrKey<T> key(Class<?> clazz, String object) {
        String key = clazz + object;
        AttrKey<T> value = (AttrKey<T>) KEY_MAP.get(key);
        if (value == null) {
            value = new DefaultAttributeKey<>(object);
            AttrKey<?> old = KEY_MAP.putIfAbsent(object, value);
            if (old != null)
                value = (AttrKey<T>) old;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T> AttrKey<T> key(String object) {
        AttrKey<T> value = (AttrKey<T>) KEY_MAP.get(object);
        if (value == null) {
            value = new DefaultAttributeKey<>(object);
            AttrKey<?> old = KEY_MAP.putIfAbsent(object, value);
            if (old != null)
                value = (AttrKey<T>) old;
        }
        return value;
    }

    public static Map<String, Object> attributes2Map(Attributes attributes) {
        return attributes.getAttributeMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        e -> e.getValue()
                ));
    }

}
