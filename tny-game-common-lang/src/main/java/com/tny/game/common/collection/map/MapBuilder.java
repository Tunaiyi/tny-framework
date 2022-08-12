/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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

    public static <K, V> MapBuilder<K, V> newBuilder(MapRef<K, V> ref) {
        return new MapBuilder<>(new HashMap<>());
    }

    public static <K, V> MapBuilder<K, V> newBuilder(Map<K, V> map) {
        return new MapBuilder<>(map);
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
