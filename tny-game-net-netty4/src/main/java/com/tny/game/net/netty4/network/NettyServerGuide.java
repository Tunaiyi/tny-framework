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

    private static final EventLoopGroup parentGroup = createLoopGroup(EPOLL, 1, "Sever-Boss-LoopGroup");

    private static final EventLoopGroup childGroup = createLoopGroup(EPOLL, Runtime.getRuntime().availableProcessors() * 2, "Sever-Child-LoopGroup");

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
        return false;
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
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
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

    private void bind(final InetSocketAddress address) {
        String addressString = toAddressString(address);
        Channel channel = this.channels.get(addressString);
        if (channel != null) {
            if (channel.close().awaitUninterruptibly(30000L)) {
                this.channels.remove(addressString, channel);
            }
        }
        LOGGER.info("#NettyServer [ {} ] | 正在打开监听{}端口", this.setting.getName(), address);
        ChannelFuture channelFuture = this.bootstrap().bind(address);
        if (channelFuture.awaitUninterruptibly(30000L)) {
            this.channels.put(addressString, channelFuture.channel());
            LOGGER.info("#NettyServer [ {} ] | {}端口已监听", this.setting.getName(), address);
        } else {
            LOGGER.info("#NettyServer [ {} ] | {}端口监听失败", this.setting.getName(), address);
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
            init(this.bootstrap, parentGroup, childGroup, EPOLL);
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
