package com.tny.game.net.session.event;

import com.tny.game.net.tunnel.Tunnel;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public abstract class BaseSessionEvent<UID> implements SessionEvent<UID> {

    protected Reference<Tunnel<UID>> tunnel;

    protected BaseSessionEvent(Tunnel<UID> tunnel) {
        this.tunnel = new WeakReference<>(tunnel);
    }

    @Override
    public Tunnel<UID> getTunnel() {
        return this.tunnel.get();
    }

}
