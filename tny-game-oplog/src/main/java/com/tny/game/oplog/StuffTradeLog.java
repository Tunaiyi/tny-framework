package com.tny.game.oplog;

public interface StuffTradeLog {

    long getID();

    int getItemID();

    long getOldNum();

    long getNewNum();

    long getAlterNum();

}