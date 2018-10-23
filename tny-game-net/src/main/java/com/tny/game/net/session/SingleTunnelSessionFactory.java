package com.tny.game.net.session;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.transport.Certificate;

@Unit("SingleTunnelSessionFactory")
public class SingleTunnelSessionFactory<UID> implements SessionFactory<UID> {

    @Override
    public NetSession<UID> createSession(Certificate<UID> certificate) {
        return new SingleTunnelSession<>(certificate);
    }

}
