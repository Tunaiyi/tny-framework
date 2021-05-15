package com.tny.game.net.base;

public interface NetBootstrapSetting {

    String getName();

    NetAppContext getAppContext();

    String getMessageFactory();

    String getEndpointEventHandler();

    String getCertificateFactory();

}
