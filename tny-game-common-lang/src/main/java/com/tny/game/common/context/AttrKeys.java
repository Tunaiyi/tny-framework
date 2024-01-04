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

package com.tny.game.common.context;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AttrKeys {

    private static final ConcurrentMap<Object, AttrKey<?>> KEY_MAP = new ConcurrentHashMap<>();

    private static class DefaultAttributeKey<T> implements AttrKey<T> {

        private final String name;

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
            if (old != null) {
                value = (AttrKey<T>) old;
            }
        }
        return value;
    }

    public static Map<String, Object> attributes2Map(Attributes attributes) {
        return attributes.getAttributeMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Entry::getValue
                ));
    }

}
