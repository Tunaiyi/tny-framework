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
import com.tny.game.net.command.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * <p>
 */
public class MockNettyClient extends MockNetEndpoint implements NetTerminal<Long> {

    private URL url;

    public MockNettyClient(URL url, Certificate<Long> certificate) {
        super(certificate, NetAccessMode.CLIENT);
        this.url = url;
    }

    @Override
    public long getConnectTimeout() {
        return 0;
    }

    @Override
    public boolean isAsyncConnect() {
        return false;
    }

    @Override
    public MessageTransporter connect() {
        return new NettyChannelMessageTransporter(NetAccessMode.CLIENT, new MockChannel(new InetSocketAddress(8090), new InetSocketAddress(8091)));
    }

    @Override
    public int getConnectRetryTimes() {
        return 0;
    }

    @Override
    public List<Long> getConnectRetryIntervals() {
        return Collections.singletonList(30000L);
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void onConnected(NetTunnel<Long> tunnel) {

    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public void resend(NetTunnel<Long> tunnel, long fromId, FilterBound bound) {

    }

    @Override
    public void resend(NetTunnel<Long> tunnel, long fromId, long toId, FilterBound bound) {

    }

}
