package com.tny.game.net.initer;

public interface ServerIniter {

    InitLevel getInitLevel();

    void initialize() throws Exception;

    default boolean waitInitialized() throws Throwable {
        return true;
    }

}
