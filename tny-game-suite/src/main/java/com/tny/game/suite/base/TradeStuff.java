package com.tny.game.suite.base;

import com.tny.game.basics.item.*;

/**
 * Created by Kun Yang on 2017/4/11.
 */
public class TradeStuff {

    /**
     * 物品ID
     */
    private int itemId;

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

    public TradeStuff(int itemId, long number) {
        this(itemId, number, AlterType.UNCHECK.getId());
    }

    public TradeStuff(int itemId, long number, int alterType) {
        this.itemId = itemId;
        this.number = number;
        this.alterType = alterType;
    }

    public int getItemId() {
        return itemId;
    }

    public long getNumber() {
        return number;
    }

    public int getAlterType() {
        return alterType;
    }

    public TradeStuff setItemId(int itemId) {
        this.itemId = itemId;
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
