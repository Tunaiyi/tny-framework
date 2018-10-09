package com.tny.game.net.transport;

import com.tny.game.net.base.annotation.Unit;

@Unit("MultiTunnelSessionFactory")
public class MultiTunnelSessionFactory<UID> implements SessionFactory<UID> {

    private MultiTunnelSessionConfigurer configurer;

    public MultiTunnelSessionFactory(MultiTunnelSessionConfigurer configurer) {
        this.configurer = configurer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSession<UID> createSession(Certificate<UID> certificate) {
        return new MultiTunnelSession<>(certificate, configurer);
    }

}
