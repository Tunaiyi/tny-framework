package com.tny.game.zookeeper;

import com.tny.game.zookeeper.ZKMonitorClient.*;

public interface MonitorTask {

    RetryPolicy getPolicy();

    String getPath();

    MonitorState getState();

    MonitorOperation getOperation();

    void cancel();

    boolean isWorking();

    void start();

}
