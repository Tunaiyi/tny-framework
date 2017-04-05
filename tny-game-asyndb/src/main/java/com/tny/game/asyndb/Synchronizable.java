package com.tny.game.asyndb;

public interface Synchronizable {

    AsyncDBState getState();

    Object getValue();

    TrySyncDone trySync();

    Synchronizer<?> getSynchronizer();

    boolean syncFail(AsyncDBState currentState);

}
