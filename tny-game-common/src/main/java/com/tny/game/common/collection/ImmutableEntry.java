package com.tny.game.common.collection;

import java.io.Serializable;
import java.util.Map.Entry;

public class ImmutableEntry<K, V> implements Entry<K, V>, Serializable {

    private static final long serialVersionUID = 1L;

    private final K key;
    private final V value;

    public static <K, V> ImmutableEntry<K, V> entry(K key, V value) {
        return new ImmutableEntry<K, V>(key, value);
    }

    private ImmutableEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public final K getKey() {
        return key;
    }

    @Override
    public final V getValue() {
        return value;
    }

    @Override
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
