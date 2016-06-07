package com.tny.game.zookeeper;

import org.apache.zookeeper.Watcher.Event.KeeperState;

public interface RenewTask {

    void renew(KeeperState state, ZKClient ZKClient);

}
