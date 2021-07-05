package com.tny.game.net.agency;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 12:02 下午
 */
public class GeneralTubule<UID> extends BaseServerTunnel<UID, NetSession<UID>, TubuleTransporter<UID>> implements NetTubule<UID> {

    private final InetSocketAddress remoteAddress;

    protected GeneralTubule(long id, TubuleTransporter<UID> transporter, InetSocketAddress remoteAddress, NetBootstrapContext<UID> bootstrapContext) {
        super(id, transporter, TunnelMode.SERVER, bootstrapContext);
        this.remoteAddress = remoteAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public void pong() {
        this.transporter.pong();
    }

}
