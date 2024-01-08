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
                }
            });
        } catch (Throwable e) {
            LOGGER.error("", e);
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
