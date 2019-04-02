package com.tny.game.net.base;

public interface NetBootstrapSetting {

    String getName();

    AppContext getAppContext();

    String getMessageFactory();

    // String getMessageHandler();

    String getEventHandler();

    // MessageDispatcher getMessageDispatcher();

    // DispatchCommandExecutor getCommandExecutor();

}
