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

    /* 实例排他持有（net-guide-lifecycle）：懒建、close 释放、可重建 */
    private volatile EventLoopGroup workerGroup;

    // volatile：DCL 快路径在 synchronized 外读（对齐 server 侧既有修复）
    private volatile Bootstrap bootstrap = null;

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
            Bootstrap bootstrap = new Bootstrap();
            NettyMessageHandler messageHandler = new NettyMessageHandler(this.getContext());
            long connectTimeout = setting.getConnector().getConnectTimeout();
            bootstrap.group(ensureWorkerGroup()).channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true).option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
            if (connectTimeout > 0 && connectTimeout <= Integer.MAX_VALUE) {
                // 连接超时有界（net-tunnel 契约）：黑洞地址下不得穿透到 OS 级分钟超时
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout);
            }
            bootstrap
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
            this.bootstrap = bootstrap; // 构建完成后经 volatile 字段安全发布
            return bootstrap;
        }

    }

    @Override
    public CompletionStageFuture<NetTunnel> connectAsync(URL url, TunnelUnavailableWatch watch) {
        var future = new CompleteStageFuture<NetTunnel>();
        if (isClosed()) {
            // 关闭后拒绝新建：重连竞态不得产生"关而不掉"的活连接
            future.completeExceptionally(new TunnelException("client {} closed, connect {} rejected!", this.setting.getName(), url));
            return future;
        }
        Asserts.checkNotNull(url, "url is null");
        var channelFuture = this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
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
                if (isClosed()) {
                    // close() 快照可能恰未含本条：入组后复查自愈
                    tunnel.close();
                }
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
        this.bootstrap = null; // 构建器固化旧组引用必须随组失效；重开经 DCL 重建（net-guide-lifecycle）
        if (shuttingDownWorkerGroup != null) {
            shuttingDownWorkerGroup.shutdownGracefully();
        }
            return true;
        }
        return false;
    }


    @Override
    public NetTunnel connect(URL url, TunnelUnavailableWatch watch) throws ExecutionException, InterruptedException {
        return connectAsync(url, watch).get();
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
