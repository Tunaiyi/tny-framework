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
import com.tny.game.common.event.*;
import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.net.application.*;
import com.tny.game.net.application.listener.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.rpc.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.*;

public class NettyRelayServerGuide extends NettyServerBootstrap<NettyRelayServerBootstrapSetting> implements RelayServerGuide {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NettyRelayServerGuide.class);

    private static final boolean EPOLL = isEpoll();

    /* 实例排他持有（net-guide-lifecycle）：懒建、close 释放、可重建 */
    private volatile EventLoopGroup parentGroup;

    /* 实例排他持有（net-guide-lifecycle）：懒建、close 释放、可重建 */
    private volatile EventLoopGroup childGroup;

    private volatile ServerBootstrap bootstrap;

    private final InetSocketAddress bindAddress;

    private final InetSocketAddress serveAddress;

    private ServerRelayExplorer localRelayExplorer;

    private final Map<String, Channel> channels = new CopyOnWriteMap<>();

    /**
     * 服务器关闭监听器
     */
    private final VoidBindEvent<ServerClosedListener, ServerGuide> onClose = Events.ofEvent(
            ServerClosedListener.class, ServerClosedListener::onClosed);

    public NettyRelayServerGuide(NetAppContext appContext, NettyRelayServerBootstrapSetting unitSetting) {
        super(appContext, unitSetting);
        this.bindAddress = this.setting.bindAddress();
        this.serveAddress = this.setting.serveAddress();
    }

    public NettyRelayServerGuide(NetAppContext appContext, NettyRelayServerBootstrapSetting unitSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, unitSetting, channelMaker);
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

    @Override
    public void open() {
        this.bind(this.bindAddress);
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean close() {
        LOGGER.info("#NettyRelayServer [ {} ] | 正在关闭服务器......", this.setting.getName());
        this.channels.forEach((address, channel) -> {
            try {
                NettyRelayServerGuide.LOGGER.info("#NettyRelayServer [ {} ] | Channel {} 关闭中......", this.setting.getName(), channel);
                channel.disconnect();
                NettyRelayServerGuide.LOGGER.info("#NettyRelayServer [ {} ] | Channel {} 关闭完成", this.setting.getName(), channel);
            } catch (Throwable e) {
                NettyRelayServerGuide.LOGGER.error("#NettyRelayServer [ {} ] | Channel {} 关闭异常!!!", this.setting.getName(), channel, e);
                LOGGER.error("NettyRelayServer [ {} ] | {} close exception", this.setting.getName(), address, e);
            }
        });
        EventLoopGroup shuttingDownParentGroup = this.parentGroup;
        this.parentGroup = null;
        this.bootstrap = null; // 构建器固化旧组引用，必须随组一并失效（net-guide-lifecycle 履行）
        if (shuttingDownParentGroup != null) {
            shuttingDownParentGroup.shutdownGracefully();
        }
        EventLoopGroup shuttingDownChildGroup = this.childGroup;
        this.childGroup = null;
        if (shuttingDownChildGroup != null) {
            shuttingDownChildGroup.shutdownGracefully();
        }
        NettyRelayServerGuide.this.fireServerClosed();
        NettyRelayServerGuide.LOGGER.info("#NettyRelayServer [ {} ] | 服务器已关闭!!!", this.setting.getName());
        return true;
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

    private void bind(final InetSocketAddress address) {
        String addressString = toAddressString(address);
        Channel channel = this.channels.get(addressString);
        if (channel != null) {
            if (channel.close().awaitUninterruptibly(30000L)) {
                this.channels.remove(addressString, channel);
            }
        }
        LOGGER.info("#NettyRelayServer [ {} ] | 正在打开监听{}端口", this.setting.getName(), address);
        ChannelFuture channelFuture = this.bootstrap().bind(address);
        if (channelFuture.awaitUninterruptibly(30000L)) {
            this.channels.put(addressString, channelFuture.channel());
            LOGGER.info("#NettyRelayServer [ {} ] | {}端口已监听", this.setting.getName(), address);
        } else {
            LOGGER.info("#NettyRelayServer [ {} ] | {}端口监听失败", this.setting.getName(), address);
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
            RelayPacketProcessor relayPacketProcessor = new RelayPacketServerProcessor(this.localRelayExplorer, this.getContext());
            NettyRelayPacketHandler relayMessageHandler = new NettyRelayPacketHandler(setting, relayPacketProcessor);
            init(this.bootstrap, ensureParentGroup(), ensureChildGroup(), EPOLL);
            this.bootstrap.childHandler(new ChannelInitializer<>() {

                @Override
                protected void initChannel(@Nonnull Channel channel) throws Exception {
                    try {
                        ChannelMaker<Channel> maker = NettyRelayServerGuide.this.channelMaker;
                        if (maker != null) {
                            maker.initChannel(channel);
                        }
                        channel.pipeline().addLast("nettyTransitDatagramHandler", relayMessageHandler);
                        createRelayChannelTransmitter(channel);
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
    protected void onLoadUnit(NettyRelayServerBootstrapSetting setting) {
        this.localRelayExplorer = UnitLoader.getLoader(ServerRelayExplorer.class).checkUnit();
    }

    private void createRelayChannelTransmitter(Channel channel) {
        new NettyChannelRelayTransport(NetAccessMode.SERVER, channel, this.getContext());
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
}
