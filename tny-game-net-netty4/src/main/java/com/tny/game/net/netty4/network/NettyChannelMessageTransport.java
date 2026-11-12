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

import com.tny.game.common.runtime.*;
import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.netty4.network.NettyNetAttrKeys.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 2:26 下午
 */
public class NettyChannelMessageTransport extends NettyChannelConnection implements MessageTransport {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelMessageTransport.class);

    public NettyChannelMessageTransport(NetAccessMode accessMode, Channel channel) {
        super(accessMode, channel);
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public MessageWriteFuture write(Message message, MessageWriteFuture awaiter) throws NetException {
        ChannelPromise channelPromise = createChannelPromise(awaiter);
        this.channel.writeAndFlush(message, channelPromise);
        return awaiter;
    }

    @Override
    public MessageWriteFuture write(MessageAllocator maker, MessageFactory factory, MessageContent content) throws NetException {
        MessageWriteFuture awaiter = content.getWriteFuture();
        ProcessTracer tracer = NetLogger.NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER.trace();
        try {
            this.channel.eventLoop().execute(() -> {
                try {
                    Message message = null;
                    try {
                        message = maker.allocate(factory, content);
                    } catch (Throwable e) {
                        LOGGER.error("", e);
                        if (awaiter != null) {
                            awaiter.completeExceptionally(e);
                        }
                    }
                    if (message != null) {
                        ChannelPromise channelPromise = createChannelPromise(awaiter);
                        this.channel.writeAndFlush(message, channelPromise);
                    }
                    tracer.done();
                } catch (Throwable e) {
                    LOGGER.error("", e);
                    if (awaiter != null) {
                        awaiter.completeExceptionally(e);
                    }
                }
            });
        } catch (Throwable e) {
            // 提交被拒（event-loop 终止/关闭竞态）：写回执与响应等待必须以失败终结，不得悬挂
            LOGGER.error("", e);
            if (awaiter != null) {
                awaiter.completeExceptionally(e);
            }
            content.cancel(e);
        }
        return awaiter;
    }

    @Override
    protected void doClose() {
        NetTunnel tunnel = as(this.channel.attr(TUNNEL).getAndSet(null));
        if (tunnel != null && (tunnel.isOpen() || tunnel.isActive())) {
            tunnel.disconnect();
        }
    }

    @Override
    public void bind(NetTunnel tunnel) {
        this.channel.attr(TUNNEL).set(tunnel);
    }

}
