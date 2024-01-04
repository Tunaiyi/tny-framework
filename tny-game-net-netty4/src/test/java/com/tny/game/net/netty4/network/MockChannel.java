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

import io.netty.channel.embedded.EmbeddedChannel;

import java.net.SocketAddress;

/**
 * <p>
 */
public class MockChannel extends EmbeddedChannel {

    private volatile SocketAddress localAddress;

    private volatile SocketAddress remoteAddress;

    public MockChannel(SocketAddress localAddress, SocketAddress remoteAddress) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public SocketAddress localAddress() {
        return isActive() ? localAddress : null;
    }

    @Override
    public SocketAddress remoteAddress() {
        return isActive() ? remoteAddress : null;
    }

}
