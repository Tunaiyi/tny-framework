package com.tny.game.asyndb;

public interface Synchronizable {

    public AsyncDBState getState();

    public Object getValue();

    public TrySyncDone trySync();

    public Synchronizer<?> getSynchronizer();

    public boolean syncFail(AsyncDBState currentState);

}
