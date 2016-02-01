package com.tny.game.base.item;

import com.tny.game.base.item.behavior.DemandParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DemandParamEntryBuilder {

    private List<DemandParamEntry<?>> entryList = new ArrayList<DemandParamEntry<?>>();

    private DemandParamEntryBuilder() {
    }

    public static final DemandParamEntryBuilder newBuilder() {
        return new DemandParamEntryBuilder();
    }

    public <T> DemandParamEntryBuilder put(DemandParam param, T value) {
        this.entryList.add(new DemandParamEntry<T>(param, value));
        return this;
    }

    public DemandParamEntry<?>[] buildArray() {
        return entryList.toArray(new DemandParamEntry<?>[entryList.size()]);
    }

    public Collection<DemandParamEntry<?>> buildEntries() {
        return Collections.unmodifiableCollection(this.entryList);
    }

}
