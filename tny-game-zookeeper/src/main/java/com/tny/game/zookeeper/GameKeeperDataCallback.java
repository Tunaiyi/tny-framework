package com.tny.game.zookeeper;

import org.apache.zookeeper.data.Stat;

public interface GameKeeperDataCallback<T> {

    public void processResult(int rc, String path, Object ctx, T data, Stat stat);

}
