package com.tny.game.net.transport;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-19 14:26
 */
public class AnonymousEndpoint<UID> extends AbstractEndpoint<UID> {

    private NetTunnel<UID> tunnel;

    protected AnonymousEndpoint(Certificate<UID> certificate) {
        super(certificate, MessageIdCreator.TUNNEL_MESSAGE_ID_MARK);
    }


    @Override
    protected NetTunnel<UID> selectTunnel(MessageSubject subject, MessageContext<UID> messageContext) {
        return this.tunnel;
    }

    @Override
    public void onDisable(NetTunnel<UID> tunnel) {

    }

    @Override
    public void heartbeat() {
        this.tunnel.ping();
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() {

    }
}
