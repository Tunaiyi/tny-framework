package com.tny.game.net.base.listener;

public interface SessionListener<T> {

    default void handleAddSession(SessionChangeEvent<T> event) {
    }

    default void handleRemoveSession(SessionChangeEvent<T> event) {
    }

    default void handleDisconnectSession(SessionChangeEvent<T> event) {
    }

}
