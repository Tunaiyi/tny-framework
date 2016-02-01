package com.tny.game.zookeeper;

import com.tny.game.zookeeper.GameKeeperMonitor.MonitorOperation;

public interface MonitorTask {

    public RetryPolicy getPolicy();

    public String getPath();

    public MonitorState getState();

    public MonitorOperation getOperation();

    public void cancel();

    public boolean isWorking();

}
