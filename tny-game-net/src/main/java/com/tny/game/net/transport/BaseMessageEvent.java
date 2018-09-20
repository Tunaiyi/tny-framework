package com.tny.game.net.transport;

/**
 * Created by Kun Yang on 2017/3/18.
 */
public abstract class BaseMessageEvent<UID> implements MessageEvent<UID> {

    protected NetTunnel<UID> tunnel;

    protected BaseMessageEvent(NetTunnel<UID> tunnel) {
        this.tunnel = tunnel;
    }

    @Override
    public NetTunnel<UID> getTunnel() {
        return this.tunnel;
    }

}
