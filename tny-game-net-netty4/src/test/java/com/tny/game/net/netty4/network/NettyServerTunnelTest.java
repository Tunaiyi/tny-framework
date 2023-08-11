/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.netty4.network;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NetSession<Long>, TestGeneralServerTunnel, MockNetEndpoint> {

    public static final NetIdGenerator ID_GENERATOR = new AutoIncrementIdGenerator();

    @Override
    protected TunnelTestInstance<TestGeneralServerTunnel, MockNetEndpoint> create(Certificate<Long> certificate, boolean open) {
        MockNetEndpoint endpoint = createEndpoint(certificate);
        TestGeneralServerTunnel tunnel = this.newTunnel(open);
        tunnel.bind(endpoint);
        //        if (certificate.isAuthenticated()) {
        //            tunnel.bind(endpoint);
        //        }
        return new TunnelTestInstance<>(tunnel, endpoint);
    }

    @Override
    protected MockNetEndpoint createEndpoint(Certificate<Long> certificate) {
        return new MockNetEndpoint(certificate, NetAccessMode.SERVER);
    }

    private TestGeneralServerTunnel newTunnel(boolean open) {
        TestGeneralServerTunnel tunnel = new TestGeneralServerTunnel(ID_GENERATOR.generate(),
                new NettyChannelMessageTransporter(NetAccessMode.SERVER, mockChannel()),
                new NetBootstrapContext(null, null, null, new SerialCommandExecutorFactory(), new CommonMessageFactory(), new DefaultMessagerFactory(),
                        new DefaultCertificateFactory<Long>(), null, new RpcMonitor()));
        if (open) {
            tunnel.open();
        }
        return tunnel;
    }

    @Override
    protected EmbeddedChannel embeddedChannel(TestGeneralServerTunnel tunnel) {
        return (EmbeddedChannel)((NettyChannelMessageTransporter)tunnel.getTransporter()).getChannel();
    }

}