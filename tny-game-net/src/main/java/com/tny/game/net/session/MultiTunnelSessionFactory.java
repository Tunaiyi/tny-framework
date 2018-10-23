package com.tny.game.net.session;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.transport.Certificate;

@Unit("MultiTunnelSessionFactory")
public class MultiTunnelSessionFactory<UID> implements SessionFactory<UID> {

    private int maxTunnelSize;

    public MultiTunnelSessionFactory(int maxTunnelSize) {
        this.maxTunnelSize = maxTunnelSize;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NetSession<UID> createSession(Certificate<UID> certificate) {
        return new MultiTunnelSession<>(certificate, maxTunnelSize);
    }

}
