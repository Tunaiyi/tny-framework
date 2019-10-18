package com.tny.game.oplog;

import org.joda.time.DateTime;

public interface Log {

    String getLogId();

    long getUserId();

    String getName();

    int getServerId();

    long getAt();

    int getDate();

    int getVip();

    int getLevel();

    String getOpenId();

    String getPF();

    String getType();

    DateTime getLogAt();

}
