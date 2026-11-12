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

package com.tny.game.common.collection.map;

import java.util.*;
import java.util.function.Supplier;

public class MapBuilder<K, V> {

    public Map<K, V> map;

    private MapBuilder(Map<K, V> map) {
        super();
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> newBuilder(K key, V value) {
        Map<K, V> map = new HashMap<>();
        return new MapBuilder<>(map)
                .put(key, value);
    }

    public static <K, V> MapBuilder<K, V> newBuilder() {
        return new MapBuilder<>(new HashMap<>());
    }

    public static <K, V> MapBuilder<K, V> newBuilder(Map<K, V> map) {
        // 装载入参内容并隔离：产物与源映射双向不穿透（原直接包装活引用）
        return new MapBuilder<>(new HashMap<>(map));
    }

    public static <K, V> MapBuilder<K, V> newBuilder(Supplier<Map<K, V>> supplier) {
        return new MapBuilder<>(supplier.get());
    }

    public MapBuilder<K, V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    public MapBuilder<K, V> remove(K key) {
        this.map.remove(key);
        return this;
    }

    public Map<K, V> build() {
        return this.map;
    }

}
