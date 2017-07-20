package com.tny.game.oplog;

import org.joda.time.DateTime;

public interface Loggable {

    String getLogID();

    long getUserID();

    long getAt();

    int getDate();

    int getServerID();

    int getVip();

    String getOpenID();

    String getType();

    DateTime getLogAt();

}
