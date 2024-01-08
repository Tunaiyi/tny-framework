package com.tny.game.net.transport;

import java.util.List;

public interface TunnelConnector {

    boolean isAutoReconnect();

    long getConnectTimeout();

    int getConnectRetryTimes();

    List<Long> getConnectRetryIntervals();

}
