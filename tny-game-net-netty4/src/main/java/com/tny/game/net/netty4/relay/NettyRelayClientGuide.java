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

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

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
            this.bootstrap.group(workerGroup).channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
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
            workerGroup.shutdownGracefully();
            return true;
        }
        return false;
    }

    private RelayTransport createNetRelayLink(Channel channel) {
        return new NettyChannelRelayTransport(NetAccessMode.CLIENT, channel, this.getContext());
    }

}