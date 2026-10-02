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

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyRelayClientGuide extends NettyBootstrap<NettyRelayClientBootstrapSetting> implements RelayClientGuide {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    /* 实例排他持有（net-guide-lifecycle）：懒建、close 释放、可重建 */
    private volatile EventLoopGroup workerGroup;

    private Bootstrap bootstrap = null;

    private final Set<NetTunnel> tunnels = new ConcurrentHashSet<>();

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private ClientRelayExplorer localRelayExplorer;

    public NettyRelayClientGuide(NetAppContext appContext, NettyRelayClientBootstrapSetting clientSetting) {
        super(appContext, clientSetting);
    }

    public NettyRelayClientGuide(NetAppContext appContext, NettyRelayClientBootstrapSetting clientSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, clientSetting, channelMaker);
    }

    private String clientKey(URL url) {
        return url.getHost() + ":" + url.getPort();
    }

    @Override
    public void connect(URL url, long timeout, RelayConnectCallback callback) {
        Asserts.checkNotNull(url, "url is null");
        ChannelFuture channelFuture = this.bootstrap()
                .connect(new InetSocketAddress(url.getHost(), url.getPort()));
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                callback.complete(true, url, createNetRelayLink(channelFuture.channel()), null);
            } else {
                callback.complete(false, url, null, future.cause());
            }
        });
    }


    @Override
    protected void onLoadUnit(NettyRelayClientBootstrapSetting setting) {
        this.localRelayExplorer = UnitLoader.getLoader(ClientRelayExplorer.class).checkUnit();
    }

    private Bootstrap bootstrap() {
        if (this.bootstrap != null) {
            return this.bootstrap;
        }
        synchronized (this) {
            if (this.bootstrap != null) {
                return this.bootstrap;
            }
            this.bootstrap = new Bootstrap();
            RelayPacketProcessor relayPacketProcessor = new RelayPacketClientProcessor(this.localRelayExplorer, getContext());
            NettyRelayPacketHandler relayMessageHandler = new NettyRelayPacketHandler(setting, relayPacketProcessor);
            this.bootstrap.group(ensureWorkerGroup()).channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<>() {

                        @Override
                        protected void initChannel(@Nonnull Channel channel) throws Exception {
                            try {
                                if (NettyRelayClientGuide.this.channelMaker != null) {
                                    NettyRelayClientGuide.this.channelMaker.initChannel(channel);
                                }
                                channel.pipeline().addLast("nettyMessageHandler", relayMessageHandler);
                            } catch (Throwable e) {
                                LOGGER.info("init {} channel exception", channel, e);
                                throw e;
                            }
                        }

                    });
            return this.bootstrap;
        }

    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    @Override
    public boolean close() {
        if (this.closed.compareAndSet(false, true)) {
            this.tunnels.forEach(Tunnel::close);
            EventLoopGroup shuttingDownWorkerGroup = this.workerGroup;
        this.workerGroup = null;
        if (shuttingDownWorkerGroup != null) {
            shuttingDownWorkerGroup.shutdownGracefully();
        }
            return true;
        }
        return false;
    }

    private RelayTransport createNetRelayLink(Channel channel) {
        return new NettyChannelRelayTransport(NetAccessMode.CLIENT, channel, this.getContext());
    }


    EventLoopGroup ensureWorkerGroup() {
        EventLoopGroup group = this.workerGroup;
        if (group != null && !group.isShuttingDown()) {
            return group;
        }
        synchronized (this) {
            group = this.workerGroup;
            if (group == null || group.isShuttingDown()) {
                group = this.workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup-" + this.setting.getName());
            }
            return group;
        }
    }
}
