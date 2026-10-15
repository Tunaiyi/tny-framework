/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.netty4.network;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.forkjoin.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * Created by Kun Yang on 2018/8/25.
 */
public class NettyServerTunnelTest extends NettyTunnelTest<NetSession, TestGeneralServerTunnel, MockNetSession> {

    public static final NetIdGenerator ID_GENERATOR = new AutoIncrementIdGenerator();

    @Override
    protected TunnelTestInstance<TestGeneralServerTunnel, MockNetSession> create(Certificate certificate, boolean open) {
        MockNetSession session = createSession(certificate);
        TestGeneralServerTunnel tunnel = this.newTunnel(open);
        tunnel.bind(session);
        //        if (certificate.isAuthenticated()) {
        //            tunnel.bind(session);
        //        }
        return new TunnelTestInstance<>(tunnel, session);
    }

    @Override
    protected MockNetSession createSession(Certificate certificate) {
        return new MockNetSession(certificate, NetAccessMode.SERVER);
    }

    private TestGeneralServerTunnel newTunnel(boolean open) {
        TestGeneralServerTunnel tunnel = new TestGeneralServerTunnel(ID_GENERATOR.generate(),
                new NettyChannelMessageTransport(NetAccessMode.SERVER, mockChannel()),
                new NetBootstrapContext(null, null, null, new DefaultCommandExecutorFactory(), new CommonMessageFactory(),
                        null, new DefaultContactFactory(), null, new RpcMonitor()));
        if (open) {
            tunnel.open();
        }
        return tunnel;
    }

    @Override
    protected EmbeddedChannel embeddedChannel(TestGeneralServerTunnel tunnel) {
        return (EmbeddedChannel) ((NettyChannelMessageTransport) tunnel.getTransport()).getChannel();
    }

}