package com.tny.game.asyndb;

public interface PersistentObject {

    AsyncDBState getState();

    Object getObject();

    TrySyncDone trySync();

    Synchronizer<?> getSynchronizer();

    void syncFail(AsyncDBState currentState);

}
