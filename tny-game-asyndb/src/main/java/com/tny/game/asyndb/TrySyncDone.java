package com.tny.game.asyndb;

public class TrySyncDone {

    public static final TrySyncDone FAIL = new TrySyncDone(false);

    private boolean sync;

    private AsyncDBState state;

    private Object value;

    private TrySyncDone(boolean sync) {
        this.sync = sync;
    }

    public TrySyncDone(AsyncDBState state, Object value) {
        this.sync = state.hasOperation();
        this.state = state;
        this.value = value;
    }

    public boolean isSync() {
        return this.sync;
    }

    public AsyncDBState getState() {
        return this.state;
    }

    public Object getValue() {
        return this.value;
    }

}
