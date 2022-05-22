package com.tny.game.net.base;

import org.apache.commons.lang3.StringUtils;

public interface NetBootstrapSetting {

    /**
     * @return 服务名
     */
    String getName();

    /**
     * @return 是否可转发
     */
    boolean isForwardable();

    /**
     * @return 服务发现 服务名
     */
    String getServeName();

    /**
     * @return 服务名
     */
    String serviceName();

    String getTunnelIdGenerator();

    String getMessageFactory();

    String getMessagerFactory();

    String getCertificateFactory();

    String getMessageDispatcher();

    String getCommandTaskProcessor();

    String getRpcForwarder();

    default RpcServiceType getRpcServiceType() {
        String name = serviceName();
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return RpcServiceTypes.ofService(name);
    }

}
