package com.tny.game.common.context;

import java.util.*;

public class AttrEntries {

    private List<AttrEntry<?>> entryList = new ArrayList<>();

    private AttrEntries() {
    }

    public static final <T> AttrEntry<T> create(AttrKey<T> key, T value) {
        return new AttrEntry<>(key, value);
    }

    public static final AttrEntries newBuilder() {
        return new AttrEntries();
    }

    public <T> AttrEntries put(AttrKey<T> key, T value) {
        this.entryList.add(new AttrEntry<>(key, value));
        return this;
    }

    public AttrEntry<?>[] build() {
        return this.entryList.toArray(new AttrEntry<?>[this.entryList.size()]);
    }

    public Collection<AttrEntry<?>> buildEntries() {
        return Collections.unmodifiableCollection(this.entryList);
    }

}
