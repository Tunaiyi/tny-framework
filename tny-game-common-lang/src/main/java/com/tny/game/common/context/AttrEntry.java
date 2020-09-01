package com.tny.game.common.context;

public class AttrEntry<T> {

    protected AttrKey<? extends T> key;

    protected T value;

    protected AttrEntry(AttrKey<? extends T> key, T value) {
        this.key = key;
        this.value = value;
    }

    public AttrKey<? extends T> getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

}
