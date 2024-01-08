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
