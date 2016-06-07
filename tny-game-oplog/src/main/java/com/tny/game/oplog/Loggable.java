package com.tny.game.oplog;

public interface Loggable {

    String getLogID();

    long getUserID();

    long getAt();

    int getDate();

    int getServerID();

    String getType();

}
