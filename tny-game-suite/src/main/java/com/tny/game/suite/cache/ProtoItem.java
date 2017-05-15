package com.tny.game.suite.cache;

import com.tny.game.base.item.Item;

public class ProtoItem {

    private byte[] item;

    private Object object;

    private Integer number;

    private int state;

    public ProtoItem(byte[] item, Item<?> object, Integer number, int state) {
        this.item = item;
        this.object = object;
        this.number = number;
        this.state = state;
    }

    public byte[] getItem() {
        return this.item;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Object getObject() {
        return this.object;
    }

}
