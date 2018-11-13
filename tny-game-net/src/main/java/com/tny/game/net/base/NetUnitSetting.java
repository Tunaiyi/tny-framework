package com.tny.game.net.base;

public interface NetUnitSetting {

    String getName();

    AppContext getAppContext();

    String getMessageFactory();

    String getMessageHandler();

    // MessageDispatcher getMessageDispatcher();

    // DispatchCommandExecutor getDispatchCommandExecutor();

}
