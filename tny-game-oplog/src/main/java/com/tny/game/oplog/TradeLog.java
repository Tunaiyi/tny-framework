package com.tny.game.oplog;

/**
 * 交易日志
 *
 * @author KGTny
 */
public interface TradeLog {

    byte getTradeType();

    long getID();

    int getItemID();

    long getOldNum();

    long getNewNum();

    long getAlter();

}
