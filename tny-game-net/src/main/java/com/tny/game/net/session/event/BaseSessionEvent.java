package com.tny.game.net.session.event;

import com.tny.game.net.tunnel.Tunnel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public abstract class BaseSessionEvent<UID> implements SessionEvent<UID> {

    protected Reference<Tunnel<UID>> tunnel;

    protected BaseSessionEvent(Tunnel<UID> tunnel) {
        this.tunnel = new WeakReference<>(tunnel);
    }

    @Override
    public Optional<Tunnel<UID>> getTunnel() {
        return Optional.ofNullable(this.tunnel.get());
    }

}
