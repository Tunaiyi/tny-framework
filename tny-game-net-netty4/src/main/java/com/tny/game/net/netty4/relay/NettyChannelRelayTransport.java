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
package com.tny.game.net.netty4.relay;

import com.tny.game.net.application.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import java.util.function.Consumer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:05 下午
 */
public class NettyChannelRelayTransport extends NettyChannelConnection implements RelayTransport {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelMessageTransport.class);

    private final NetworkContext context;

    public NettyChannelRelayTransport(NetAccessMode accessMode, Channel channel, NetworkContext context) {
        super(accessMode, channel);
        this.context = context;
        this.channel.attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).setIfAbsent(this);
        this.channel.closeFuture().addListener(f -> this.close());
    }

    @Override
    protected void doClose() {
        NetRelayLink link = this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).getAndSet(null);
        if (link != null) {
            link.disconnect();
        }
        this.channel.disconnect();
    }

    @Override
    public MessageWriteFuture write(RelayPacket<?> packet, MessageWriteFuture awaiter) {
        ChannelPromise channelPromise = createChannelPromise(awaiter);
        channelPromise.addListener(future -> {
            if (!future.isSuccess()) {
                // 写失败/通道关闭竞态：载荷在此终结释放（release 幂等，成功路径编码器已释放则二次无害）
                RelayPacket.release(packet);
            }
        });
        try {
            this.channel.writeAndFlush(packet, channelPromise);
        } catch (Throwable e) {
            RelayPacket.release(packet);
            channelPromise.tryFailure(e);
        }
        return awaiter;
    }

    @Override
    public MessageWriteFuture write(RelayPacketMaker maker, MessageWriteFuture awaiter) {
        ChannelPromise channelPromise = createChannelPromise(awaiter);
        try {
            this.channel.eventLoop().execute(() -> {
                RelayPacket<?> made;
                try {
                    made = maker.make();
                } catch (Throwable e) {
                    channelPromise.tryFailure(e);
                    return;
                }
                final RelayPacket<?> packet = made;
                channelPromise.addListener(f -> {
                    if (!f.isSuccess()) {
                        RelayPacket.release(packet);
                    }
                });
                this.channel.writeAndFlush(packet, channelPromise);
            });
        } catch (Throwable e) {
            // event-loop 终止拒绝提交：回执以失败完成，不得悬挂
            channelPromise.tryFailure(e);
        }
        return awaiter;
    }

    @Override
    public void bind(NetRelayLink link) {
        this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).setIfAbsent(link);
    }

    @Override
    public NetworkContext getContext() {
        return context;
    }

    @Override
    public void addCloseListener(Consumer<RelayTransport> onClose) {
        this.channel.closeFuture().addListener((f) -> onClose.accept(this));
    }

}
