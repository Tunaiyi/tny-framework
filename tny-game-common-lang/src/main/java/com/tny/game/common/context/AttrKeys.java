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
        // 原实现查询用短名 key、写入用全名 full：命名空间劫持（key(Foo,"x") 命中全局 "x"）、
        // 同 name 双实例（attributes2Map 撞 IllegalStateException）、cache-miss 反复 new。
        // 统一按全名原子创建：同 (namespace,name) 恒返回同一实例，不同命名空间互不可见。
        return (AttrKey<T>) KEY_MAP.computeIfAbsent(full, k -> new DefaultAttributeKey<>(key));
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
