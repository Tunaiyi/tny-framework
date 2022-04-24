package com.tny.game.oplog;

public interface StuffTradeLog {

    long getId();

    int getModelId();

    long getOldNum();

    long getNewNum();

    long getAlterNum();

}