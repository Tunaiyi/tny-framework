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
package com.tny.game.net.netty4.channel;

import com.tny.game.common.context.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:46 下午
 */
public abstract class NettyChannelConnection extends AttributeHolder implements Connection {

    protected Channel channel;

    private final NetAccessMode accessMode;

    private final AtomicBoolean close = new AtomicBoolean(false);

    protected NettyChannelConnection(NetAccessMode accessMode, Channel channel) {
        this.channel = channel;
        this.accessMode = accessMode;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) this.channel.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) this.channel.localAddress();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public boolean isClosed() {
        return close.get();
    }

    public NetAccessMode getAccessMode() {
        return accessMode;
    }

    @Override
    public boolean close() {
        if (close.get()) {
            return false;
        }
        if (close.compareAndSet(false, true)) {
            this.doClose();
            this.channel.disconnect();
            return true;
        }
        return false;
    }

    protected void doClose() {
    }

    protected ChannelPromise createChannelPromise(MessageWriteFuture awaiter) {
        ChannelPromise channelPromise = this.channel.newPromise();
        if (awaiter != null) {
            channelPromise.addListener(new NettyWriteMessageHandler(awaiter));
        }
        return channelPromise;
    }

    @Override
    public String toString() {
        return valueOf(this.channel);
    }

}
