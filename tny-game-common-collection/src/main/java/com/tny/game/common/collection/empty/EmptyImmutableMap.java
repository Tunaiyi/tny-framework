package com.tny.game.common.collection.empty;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.Supplier;

public class EmptyImmutableMap<K, V> implements Map<K, V> {

    private Map<K, V> map = ImmutableMap.of();

    private Supplier<Map<K, V>> creator;

    public EmptyImmutableMap() {
    }

    public EmptyImmutableMap(Supplier<Map<K, V>> creator) {
        this.creator = creator;
    }

    private Map<K, V> getWriter() {
        if (this.map instanceof ImmutableMap) {
            this.map = this.creator != null ? this.creator.get() : new HashMap<>();
        }
        return this.map;
    }

    private Map<K, V> getReader() {
        if (this.map == null) {
            this.map = ImmutableMap.of();
        }
        return this.map;
    }

    @Override
    public int size() {
        return getReader().size();
    }

    @Override
    public boolean isEmpty() {
        return getReader().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getReader().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getReader().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getReader().get(key);
    }

    @Override
    public V put(K key, V value) {
        if (key != null) {
            return getWriter().put(key, value);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        if (key != null) {
            return getWriter().remove(key);
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (!m.isEmpty()) {
            getWriter().putAll(m);
        }
    }

    @Override
    public void clear() {
        if (!this.getReader().isEmpty()) {
            getWriter().clear();
        }
    }

    @Override
    public Set<K> keySet() {
        return getReader().keySet();
    }

    @Override
    public Collection<V> values() {
        return getReader().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getReader().entrySet();
    }

}
