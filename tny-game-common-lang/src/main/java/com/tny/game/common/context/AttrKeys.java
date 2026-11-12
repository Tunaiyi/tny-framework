/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
