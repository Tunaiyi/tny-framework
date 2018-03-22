package com.tny.game.common.utils.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapBuilder<K, V> {

    public Map<K, V> map;

    private MapBuilder(Map<K, V> map) {
        super();
        this.map = map;
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