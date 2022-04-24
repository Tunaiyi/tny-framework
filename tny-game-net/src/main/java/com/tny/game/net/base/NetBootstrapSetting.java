package com.tny.game.net.base;

public interface NetBootstrapSetting {

    /**
     * @return 服务名
     */
    String getName();

    /**
     * @return 服务发现 服务名
     */
    String getServeName();

    String getTunnelIdGenerator();

    String getMessageFactory();

    String getCertificateFactory();

    String getMessageDispatcher();

    String getCommandTaskProcessor();

}
