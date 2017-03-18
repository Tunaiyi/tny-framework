package com.tny.game.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class AttrUtils {

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

    public static <T> AttrKey<T> key(Class<?> clazz, String key) {
        return loadOrCreate(clazz.getName() + "." + key, key);
    }


    public static <T> AttrKey<T> key(String key) {
        return loadOrCreate(key, key);
    }

    @SuppressWarnings("unchecked")
    private static <T> AttrKey<T> loadOrCreate(String full, String key) {
        AttrKey<T> value = (AttrKey<T>) KEY_MAP.get(key);
        if (value == null) {
            value = new DefaultAttributeKey<>(key);
            AttrKey<?> old = KEY_MAP.putIfAbsent(full, value);
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
