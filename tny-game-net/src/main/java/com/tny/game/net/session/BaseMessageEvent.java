package com.tny.game.net.session;

import com.tny.game.net.tunnel.*;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public abstract class BaseMessageEvent<UID> implements MessageEvent<UID> {

    protected Reference<NetTunnel<UID>> tunnel;

    protected BaseMessageEvent(NetTunnel<UID> tunnel) {
        this.tunnel = new WeakReference<>(tunnel);
    }

    @Override
    public Optional<NetTunnel<UID>> getTunnel() {
        return Optional.ofNullable(this.tunnel.get());
    }

}
