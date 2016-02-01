package com.tny.game.common.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AttributeEntryBuilder {

    private List<AttributeEntry<?>> entryList = new ArrayList<AttributeEntry<?>>();

    private AttributeEntryBuilder() {
    }

    public static final AttributeEntryBuilder newBuilder() {
        return new AttributeEntryBuilder();
    }

    public <T> AttributeEntryBuilder put(AttrKey<T> key, T value) {
        this.entryList.add(new AttributeEntry<T>(key, value));
        return this;
    }

    public AttributeEntry<?>[] buildArray() {
        return this.entryList.toArray(new AttributeEntry<?>[this.entryList.size()]);
    }

    public Collection<AttributeEntry<?>> buildEntries() {
        return Collections.unmodifiableCollection(this.entryList);
    }

}
