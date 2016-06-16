package com.tny.game.net.base.listener;

public interface SessionListener {

    default void handleAddSession(SessionChangeEvent event){};

    default void handleRemoveSession(SessionChangeEvent event){};

    default void handleDisconnectSession(SessionChangeEvent event){};

}
