package com.tny.game.oplog;

import org.joda.time.DateTime;

public interface Log {

    String getLogID();

    long getUserID();

    String getName();

    int getServerID();

    long getAt();

    int getDate();

    int getVip();

    int getLevel();

    String getOpenID();

    String getPF();

    String getType();

    DateTime getLogAt();

}
