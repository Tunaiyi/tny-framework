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

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.event.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.application.*;
import com.tny.game.net.application.listener.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.transport.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.*;

public class NettyServerGuide extends NettyServerBootstrap<NettyNetServerBootstrapSetting> implements ServerGuide {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyServerGuide.class);

    private static final boolean EPOLL = isEpoll();

    /* IO 线程组实例排他持有（net-guide-lifecycle 规格）：懒建、close 释放、重启可重建——
       原 static 共享导致同 JVM 多实例 close 互相扼杀且无法复活 */
    private volatile EventLoopGroup parentGroup;

    private volatile EventLoopGroup childGroup;

    private volatile ServerBootstrap bootstrap;

    private final InetSocketAddress bindAddress;

    private final InetSocketAddress serveAddress;

    private final Map<String, Channel> channels = new CopyOnWriteMap<>();

    /**
     * 服务器关闭监听器
     */
    private final VoidBindEvent<ServerClosedListener, ServerGuide> onClose = Events.ofEvent(ServerClosedListener.class,
            ServerClosedListener::onClosed);

    public NettyServerGuide(NetAppContext appContext, NettyNetServerBootstrapSetting setting) {
        super(appContext, setting);
        this.bindAddress = this.setting.bindAddress();
        this.serveAddress = this.setting.serveAddress();
    }

    @Override
    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    @Override
    public InetSocketAddress getServeAddress() {
        return serveAddress;
    }

    public NettyServerGuide(NetAppContext appContext, NettyNetServerBootstrapSetting setting, ChannelMaker<Channel> channelMaker) {
        super(appContext, setting, channelMaker);
        this.bindAddress = this.setting.bindAddress();
        this.serveAddress = this.setting.serveAddress();
    }

    @Override
    public void open() {
        this.bind(this.bindAddress);
    }

    @Override
    public boolean isBound() {
        return this.channels.values().stream().anyMatch(Channel::isOpen); // D2：真实监听状态
    }

    @Override
    public boolean close() {
        LOGGER.info("#NettyServer [ {} ] | 正在关闭服务器......", this.setting.getName());
        this.channels.forEach((address, channel) -> {
            try {
                NettyServerGuide.LOGGER.info("#NettyServer [ {} ] | Channel {} 关闭中......", this.setting.getName(), channel);
                channel.disconnect();
                NettyServerGuide.LOGGER.info("#NettyServer [ {} ] | Channel {} 关闭完成", this.setting.getName(), channel);
            } catch (Throwable e) {
                NettyServerGuide.LOGGER.error("#NettyServer [ {} ] | Channel {} 关闭异常!!!", this.setting.getName(), channel, e);
                LOGGER.error("NettyServer [ {} ] | {} close exception", this.setting.getName(), address, e);
            }
        });
        EventLoopGroup parent = this.parentGroup;
        EventLoopGroup child = this.childGroup;
        this.parentGroup = null;
        this.childGroup = null;
        this.bootstrap = null; // 构建器固化旧组引用，必须随组一并失效（net-guide-lifecycle 履行）
        if (parent != null) {
            parent.shutdownGracefully();
        }
        if (child != null) {
            child.shutdownGracefully();
        }
        NettyServerGuide.this.fireServerClosed();
        NettyServerGuide.LOGGER.info("#NettyServer [ {} ] | 服务器已关闭!!!", this.setting.getName());
        return true;
    }

    @Override
    public String getScheme() {
        return setting.getScheme();
    }

    @Override
    public void addClosedListener(final ServerClosedListener listener) {
        this.onClose.addListener(listener);
    }

    @Override
    public void addClosedListeners(final Collection<ServerClosedListener> listenerCollection) {
        listenerCollection.forEach(this.onClose::addListener);
    }

    @Override
    public void clearClosedListener() {
        this.onClose.clear();
    }

    private void fireServerClosed() {
        this.onClose.notify(this);
    }

    private String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    EventLoopGroup ensureParentGroup() {
        EventLoopGroup group = this.parentGroup;
        if (group != null && !group.isShuttingDown()) {
            return group;
        }
        synchronized (this) {
            group = this.parentGroup;
            if (group == null || group.isShuttingDown()) {
                group = this.parentGroup = createLoopGroup(EPOLL, 1, "Sever-Boss-LoopGroup-" + this.setting.getName());
            }
            return group;
        }
    }

    EventLoopGroup ensureChildGroup() {
        EventLoopGroup group = this.childGroup;
        if (group != null && !group.isShuttingDown()) {
            return group;
        }
        synchronized (this) {
            group = this.childGroup;
            if (group == null || group.isShuttingDown()) {
                group = this.childGroup = createLoopGroup(EPOLL, Runtime.getRuntime().availableProcessors() * 2, "Sever-Child-LoopGroup-" + this.setting.getName());
            }
            return group;
        }
    }

    private void bind(final InetSocketAddress address) {
        String addressString = toAddressString(address);
        // 重绑：无条件摘除并尽力关闭旧监听通道（关闭耗时不参与是否摘除的判定，D3）
        Channel oldChannel = this.channels.remove(addressString);
        if (oldChannel != null) {
            oldChannel.close().awaitUninterruptibly(30000L);
        }
        LOGGER.info("#NettyServer [ {} ] | 正在打开监听{}端口", this.setting.getName(), address);
        ChannelFuture channelFuture = this.bootstrap().bind(address);
        boolean bound = channelFuture.awaitUninterruptibly(30000L) && channelFuture.isSuccess();
        if (bound) {
            this.channels.put(addressString, channelFuture.channel());
            LOGGER.info("#NettyServer [ {} ] | {}端口已监听", this.setting.getName(), address);
        } else {
            // 失败/超时：回收半开通道防资源残留（net-guide-lifecycle 规格）
            Throwable cause = channelFuture.cause();
            channelFuture.channel().close();
            if (cause != null) {
                LOGGER.error("#NettyServer [ {} ] | {}端口监听失败: {}", this.setting.getName(), address, cause.getMessage(), cause);
            } else {
                LOGGER.error("#NettyServer [ {} ] | {}端口监听超时", this.setting.getName(), address);
            }
        }
    }

    private ServerBootstrap bootstrap() {
        if (this.bootstrap != null) {
            return this.bootstrap;
        }
        synchronized (this) {
            if (this.bootstrap != null) {
                return this.bootstrap;
            }
            this.bootstrap = new ServerBootstrap();
            NettyChannelSetting channelSetting = setting.getChannel();
            NettyMessageHandlerFactory nettyMessageHandlerFactory = UnitLoader.getLoader(NettyMessageHandlerFactory.class)
                    .checkUnit(channelSetting.getMessageHandlerFactory());
            NettyTunnelFactory tunnelFactory = UnitLoader.getLoader(NettyTunnelFactory.class)
                    .checkUnit(channelSetting.getTunnelFactory());
            var messageHandler = nettyMessageHandlerFactory.create(this.getContext());
            init(this.bootstrap, ensureParentGroup(), ensureChildGroup(), EPOLL);
            this.bootstrap.childHandler(new ChannelInitializer<>() {

                @Override
                protected void initChannel(@Nonnull Channel channel) throws Exception {
                    try {
                        ChannelMaker<Channel> maker = NettyServerGuide.this.channelMaker;
                        if (maker != null) {
                            maker.initChannel(channel);
                        }
                        channel.pipeline().addLast("nettyMessageHandler", messageHandler);
                        NetworkContext context = NettyServerGuide.this.getContext();
                        var sessionFactory = context.getSessionFactory();
                        NetTunnel tunnel = tunnelFactory.create(idGenerator.generate(), channel, context); // 创建 Tunnel 已经transport.bind
                        sessionFactory.create(context, tunnel);
                        tunnel.open();
                    } catch (Throwable e) {
                        LOGGER.info("init {} channel exception", channel, e);
                        throw e;
                    }
                }
            });
            return this.bootstrap;
        }

    }
}
