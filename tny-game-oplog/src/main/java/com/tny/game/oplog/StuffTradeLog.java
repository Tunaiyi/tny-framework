package com.tny.game.oplog;

public interface StuffTradeLog {

    long getId();

    int getItemId();

    long getOldNum();

    long getNewNum();

    long getAlterNum();

}