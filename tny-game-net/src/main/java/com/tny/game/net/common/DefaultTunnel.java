package com.tny.game.net.common;

import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.tunnel.TunnelContent;

/**
 * Created by Kun Yang on 2017/3/26.
 */
public class DefaultTunnel<UID> extends AbstractTunnel<UID> {

    public DefaultTunnel(SessionFactory<UID> sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public String getHostName() {
        return "Free";
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public void ping() {
    }

    @Override
    public void pong() {
    }

    @Override
    public void write(TunnelContent<UID> content) {
        content.cancelSendWait(true);
    }

}
