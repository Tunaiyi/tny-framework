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

import com.tny.game.common.url.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * <p>
 */
public class MockNettyClient extends MockNetSession {

    private final URL url;

    public MockNettyClient(URL url, Certificate certificate) {
        super(certificate, NetAccessMode.CLIENT);
        this.url = url;
    }

    public MessageTransport connect() {
        return new NettyChannelMessageTransport(NetAccessMode.CLIENT, new MockChannel(new InetSocketAddress(8090), new InetSocketAddress(8091)));
    }


    public List<Long> getConnectRetryIntervals() {
        return Collections.singletonList(30000L);
    }


}
