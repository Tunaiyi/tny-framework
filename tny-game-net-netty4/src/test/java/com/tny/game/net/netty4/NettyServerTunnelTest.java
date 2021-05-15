package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NetEndpoint<Long>, NettyServerTunnel<Long>, MockNetEndpoint> {

    @Override
    protected TunnelTestInstance<NettyServerTunnel<Long>, MockNetEndpoint> create(Certificate<Long> certificate, boolean open) {
        MockEndpointEventsBoxHandler<NetEndpoint<Long>> handler = new MockEndpointEventsBoxHandler<>();
        MockNetEndpoint endpoint = createEndpoint(certificate);
        NettyServerTunnel<Long> tunnel = this.newTunnel(handler, open);
        tunnel.setEndpoint(endpoint);
        if (certificate.isAuthenticated()) {
            tunnel.bind(endpoint);
        }
        return new TunnelTestInstance<>(tunnel, endpoint);
    }

    @Override
    protected MockNetEndpoint createEndpoint(Certificate<Long> certificate) {
        return new MockNetEndpoint(certificate);
    }

    private NettyServerTunnel<Long> newTunnel(MockEndpointEventsBoxHandler<NetEndpoint<Long>> handler, boolean open) {
        NettyServerTunnel<Long> tunnel = new NettyServerTunnel<>(mockChannel(),
                new NetBootstrapContext<>(handler, new CommonMessageFactory<>(), null));
        if (open) {
            tunnel.open();
        }
        return tunnel;
    }

}