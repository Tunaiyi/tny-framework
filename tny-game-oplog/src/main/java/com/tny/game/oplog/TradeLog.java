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

    int getOldNum();

    int getNewNum();

    int getAlter();

}
