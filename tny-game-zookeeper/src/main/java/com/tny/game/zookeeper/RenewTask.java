package com.tny.game.zookeeper;

import org.apache.zookeeper.Watcher.Event.KeeperState;

public interface RenewTask {

    public void renew(KeeperState state, GameKeeper gameKeeper);

}
