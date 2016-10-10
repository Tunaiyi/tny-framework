package com.tny.game.suite.cache;

import com.tny.game.base.item.Item;

public class ProtoItem {

    private byte[] item;

    private Item<?> itemObject;

    private Integer number;

    private int state;

    public ProtoItem(byte[] item, Item<?> itemObject, Integer number, int state) {
        this.item = item;
        this.itemObject = itemObject;
        this.number = number;
        this.state = state;
    }

    public byte[] getItem() {
        return this.item;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Item<?> getItemObject() {
        return this.itemObject;
    }

}
