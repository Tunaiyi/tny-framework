package com.tny.game.worker;

public interface Callback<M> {

    public void callback(boolean success, boolean executed, M message);

}
