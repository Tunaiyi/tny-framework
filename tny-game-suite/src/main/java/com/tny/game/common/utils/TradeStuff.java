package com.tny.game.common.utils;

import com.tny.game.base.item.AlterType;

/**
 * Created by Kun Yang on 2017/4/11.
 */
public class TradeStuff {

    /**
     * 物品ID
     */
    private int itemID;

    /**
     * 物品数量
     */
    private long number;

    /**
     * 更改方式 1: 检测上下限 2: 不检测上下限(可超出) 3: 忽略多出
     */
    private int alterType;

    public TradeStuff() {

    }

    public TradeStuff(int itemID, long number) {
        this(itemID, number, AlterType.UNCHECK.getID());
    }

    public TradeStuff(int itemID, long number, int alterType) {
        this.itemID = itemID;
        this.number = number;
        this.alterType = alterType;
    }

    public int getItemID() {
        return itemID;
    }

    public long getNumber() {
        return number;
    }

    public int getAlterType() {
        return alterType;
    }

    public TradeStuff setItemID(int itemID) {
        this.itemID = itemID;
        return this;
    }

    public TradeStuff setNumber(long number) {
        this.number = number;
        return this;
    }

    public TradeStuff setAlterType(int alterType) {
        this.alterType = alterType;
        return this;
    }
}
