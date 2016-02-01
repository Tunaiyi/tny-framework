package com.tny.game.oplog;

public enum OpTradeType {

    CONSUME(0),

    RECEIVE(1);

    public final byte ID;

    OpTradeType(int iD) {
        ID = (byte) iD;
    }

}
