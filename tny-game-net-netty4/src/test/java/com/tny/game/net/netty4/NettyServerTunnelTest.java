package com.tny.game.net.netty4;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NetEndpoint<Long>, NettyServerTunnel<Long>, MockNetEndpoint> {

    @Override
    protected TunnelTestInstance<NettyServerTunnel<Long>, MockNetEndpoint> create(Certificate<Long> certificate, boolean open) {
        MockEndpointEventHandler<NetEndpoint<Long>> handler = new MockEndpointEventHandler<>();
        NettyServerTunnel<Long> tunnel = this.newTunnel(handler, open);
        MockNetEndpoint endpoint = createEndpoint(certificate);
        if (certificate.isAutherized()) {
            tunnel.bind(endpoint);
        }
        return new TunnelTestInstance<>(tunnel, endpoint);
    }

    @Override
    protected MockNetEndpoint createEndpoint(Certificate<Long> certificate) {
        return new MockNetEndpoint(certificate);
    }

    private NettyServerTunnel<Long> newTunnel(MockEndpointEventHandler<NetEndpoint<Long>> handler, boolean open) {
        NettyServerTunnel<Long> tunnel = new NettyServerTunnel<>(mockChannel(), unloginUid, handler, new CommonMessageFactory<>());
        if (open)
            tunnel.open();
        return tunnel;
    }

}