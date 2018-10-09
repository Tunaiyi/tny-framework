package com.tny.game.net.transport;

import com.tny.game.net.base.annotation.Unit;

@Unit("SingleTunnelSessionFactory")
public class SingleTunnelSessionFactory<UID> implements SessionFactory<UID> {

    private SessionConfigurer configurer;

    public SingleTunnelSessionFactory(SessionConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSession<UID> createSession(Certificate<UID> certificate) {
        return new SingleTunnelSession<>(certificate, configurer);
    }

}
