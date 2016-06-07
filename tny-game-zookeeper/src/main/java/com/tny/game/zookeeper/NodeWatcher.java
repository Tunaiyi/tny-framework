package com.tny.game.zookeeper;

public interface NodeWatcher<T> {

    void notify(String path, WatchState state, T oldData, T newData);

}
