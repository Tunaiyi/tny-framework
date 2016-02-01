package com.tny.game.net.base.listener;

public interface SessionListener {

    public void handleAddSession(SessionChangeEvent event);

    public void handleRemoveSession(SessionChangeEvent event);

    public void handleDisconnectSession(SessionChangeEvent event);

}
