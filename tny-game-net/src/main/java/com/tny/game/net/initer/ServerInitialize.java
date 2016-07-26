package com.tny.game.net.initer;

public interface ServerInitialize {

    void initialize() throws Exception;

    StartIniter getStartIniter();

    default boolean waitInitialized() throws Throwable {
        return true;
    }

}
