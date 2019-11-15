package com.tny.game.base.item;

import com.tny.game.base.item.behavior.*;

public class DemandParamEntry<T> {

    protected DemandParam param;

    protected T value;

    protected DemandParamEntry(DemandParam param, T value) {
        this.param = param;
        this.value = value;
    }

    public DemandParam getParam() {
        return param;
    }

    public T getValue() {
        return value;
    }

}
