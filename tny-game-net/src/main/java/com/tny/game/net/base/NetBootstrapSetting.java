package com.tny.game.net.base;

public interface NetBootstrapSetting {

    String getName();

    NetAppContext getAppContext();

    String getMessageFactory();

    String getCertificateFactory();

    String getMessageDispatcher();

    String getCommandTaskProcessor();

}
