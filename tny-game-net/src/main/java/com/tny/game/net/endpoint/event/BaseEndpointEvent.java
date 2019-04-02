package com.tny.game.net.endpoint.event;

import com.tny.game.net.transport.*;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public abstract class BaseEndpointEvent<UID> implements EndPointEvent<UID> {

    protected Reference<NetTunnel<UID>> tunnel;

    protected BaseEndpointEvent(NetTunnel<UID> tunnel) {
        if (tunnel != null)
            this.tunnel = new WeakReference<>(tunnel);
    }

    @Override
    public Optional<NetTunnel<UID>> getTunnel() {
        if (this.tunnel == null)
            return Optional.empty();
        return Optional.ofNullable(this.tunnel.get());
    }

}
