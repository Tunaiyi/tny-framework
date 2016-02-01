package com.tny.game.oplog.simple;

import com.tny.game.base.item.Item;
import com.tny.game.oplog.OpTradeType;
import com.tny.game.oplog.TradeLog;

public class SimpleTradeLog implements TradeLog {

    private byte tradeType;

    private long id;

    private int itemID;

    private int oldNum;

    private int alter;

    private int newNum;

    public SimpleTradeLog(Item<?> item, OpTradeType tradeType, int oldNumber, int alter, int newNumber) {
        super();
        this.tradeType = tradeType.ID;
        this.id = item.getID();
        this.itemID = item.getItemID();
        this.oldNum = oldNumber;
        this.newNum = newNumber;
        this.alter = tradeType == OpTradeType.CONSUME ? -1 * alter : alter;
    }

    public SimpleTradeLog(long id, int itemID, OpTradeType tradeType, int oldNum, int alter, int newNum) {
        super();
        this.tradeType = tradeType.ID;
        this.id = id;
        this.itemID = itemID;
        this.oldNum = oldNum;
        this.newNum = newNum;
        this.alter = tradeType == OpTradeType.CONSUME ? -1 * alter : alter;
    }

    public void merge(int alter, int newNum) {
        if (this.tradeType == OpTradeType.CONSUME.ID) {
            this.alter -= alter;
        } else {
            this.alter += alter;
        }
        this.newNum = newNum;
    }

    @Override
    public byte getTradeType() {
        return this.tradeType;
    }

    @Override
    public long getID() {
        return this.id;
    }

    @Override
    public int getItemID() {
        return this.itemID;
    }

    @Override
    public int getAlter() {
        return this.alter;
    }

    @Override
    public int getOldNum() {
        return this.oldNum;
    }

    @Override
    public int getNewNum() {
        return this.newNum;
    }

}
