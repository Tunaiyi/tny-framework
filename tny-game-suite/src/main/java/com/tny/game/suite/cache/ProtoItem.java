package com.tny.game.suite.cache;

import com.tny.game.base.item.Item;

public class ProtoItem {

    private byte[] item;

    private Item<?> itemObject;

    private Integer number;

    public ProtoItem(byte[] item, Item<?> itemObject, Integer number) {
        this.item = item;
        this.itemObject = itemObject;
        this.number = number;
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
