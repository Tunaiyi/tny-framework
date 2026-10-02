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
