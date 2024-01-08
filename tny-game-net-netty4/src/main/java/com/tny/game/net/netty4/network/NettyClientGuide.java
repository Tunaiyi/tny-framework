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

import com.tny.game.common.concurrent.*;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.application.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyClientGuide extends NettyBootstrap<NettyNetClientBootstrapSetting> implements ClientGuide {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private final Set<NetTunnel> tunnels = new ConcurrentHashSet<>();

    private final AtomicBoolean closed = new AtomicBoolean(false);

    public NettyClientGuide(NetAppContext appContext, NettyNetClientBootstrapSetting clientSetting) {
        super(appContext, clientSetting);
    }

    public NettyClientGuide(NetAppContext appContext, NettyNetClientBootstrapSetting clientSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, clientSetting, channelMaker);
    }

    private String clientKey(URL url) {
        return url.getHost() + ":" + url.getPort();
    }

    private Bootstrap getBootstrap() {
        if (this.bootstrap != null) {
            return this.bootstrap;
        }
        synchronized (this) {
            if (this.bootstrap != null) {
                return this.bootstrap;
            }
            this.bootstrap = new Bootstrap();
            NettyMessageHandler messageHandler = new NettyMessageHandler(this.getContext());
            this.bootstrap.group(workerGroup).channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<>() {

                        @Override
                        protected void initChannel(@Nonnull Channel channel) throws Exception {
                            try {
                                if (NettyClientGuide.this.channelMaker != null) {
                                    NettyClientGuide.this.channelMaker.initChannel(channel);
                                }
                                channel.pipeline().addLast("nettyMessageHandler", messageHandler);
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
    public CompletionStageFuture<NetTunnel> connectAsync(URL url, TunnelUnavailableWatch watch) {
        ClientConnectorSetting setting = getSetting().getConnector();
        var future = new CompleteStageFuture<NetTunnel>();
        var channelFuture = connectAsync(url, setting.getConnectTimeout());
        channelFuture.addListener(f -> {
            if (f.isSuccess()) {
                var context = getContext();
                var channel = channelFuture.channel();
                var transport = new NettyChannelMessageTransport(NetAccessMode.CLIENT, channel);
                var tunnel = new GeneralClientTunnel(this.idGenerator.generate(), transport, this.getContext(), watch);
                var sessionFactory = context.getSessionFactory();
                sessionFactory.create(context, tunnel);
                channel.closeFuture().addListener((closeFuture) -> {
                    channel.attr(NettyNetAttrKeys.TUNNEL);
                    tunnels.remove(tunnel);
                    transport.close();
                });
                tunnel.open();
                tunnels.add(tunnel);
                future.complete(tunnel);
            } else {
                if (f.cause() != null) {
                    future.completeExceptionally(f.cause());
                } else {
                    future.completeExceptionally(new TunnelException("connect {} failed!", url));
                }
            }
        });
        return future;
    }

    private ChannelFuture connectAsync(URL url, long connectTimeout) throws NetException {
        Asserts.checkNotNull(url, "url is null");
        return this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
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


    @Override
    public NetTunnel connect(URL url, TunnelUnavailableWatch watch) throws ExecutionException, InterruptedException {
        return connectAsync(url, watch).get();
    }

}