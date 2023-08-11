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
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.transport.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.endpoint.listener.ClientEventBuses.*;

public class NettyClientGuide extends NettyBootstrap<NettyNetClientBootstrapSetting> implements ClientGuide {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private final Set<Client<?>> clients = new ConcurrentHashSet<>();

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final ClientCloseListener<Object> closeListener = this.clients::remove;

    private final ClientOpenListener<Object> openListener = this.clients::add;

    public NettyClientGuide(NetAppContext appContext, NettyNetClientBootstrapSetting clientSetting) {
        super(appContext, clientSetting);
        buses().<Object>closeEvent().addListener(this.closeListener);
        buses().<Object>openEvent().addListener(this.openListener);
    }

    public NettyClientGuide(NetAppContext appContext, NettyNetClientBootstrapSetting clientSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, clientSetting, channelMaker);
        buses().closeEvent().addListener(this.closeListener);
        buses().openEvent().addListener(this.openListener);
    }

    private String clientKey(URL url) {
        return url.getHost() + ":" + url.getPort();
    }

    @Override
    public <UID> Client<UID> client(URL url, PostConnect<UID> connect) {
        NetworkContext context = this.getContext();
        CertificateFactory<UID> certificateFactory = context.getCertificateFactory();
        return new NettyClient<>(this, this.idGenerator, url, connect, certificateFactory.anonymous(), context);
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
            this.bootstrap.group(workerGroup)
                    .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
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

    Channel connect(URL url, long connectTimeout) throws NetException {
        Asserts.checkNotNull(url, "url is null");
        ChannelFuture channelFuture = null;
        try {
            channelFuture = this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
            boolean result = channelFuture.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
            boolean success = channelFuture.isSuccess();
            if (result && success) {
                return channelFuture.channel();
            }
            boolean connected = false;
            if (channelFuture.channel() != null) {
                connected = channelFuture.channel().isActive();
            }
            if (channelFuture.cause() != null) {
                channelFuture.cancel(true);
                throw new NetException(format("Connect url: {} failed. result: {} success: {}, connected: {}", url, result, success, connected),
                        channelFuture.cause());
            } else {
                channelFuture.cancel(true);
                throw new NetException(format("Connect url: {} timeout. result: {}, success: {}, connected: {}", url, result, success, connected),
                        channelFuture.cause());
            }
        } catch (NetException e) {
            throw e;
        } catch (Exception e) {
            if (channelFuture != null) {
                channelFuture.channel().close();
            }
            throw new NetException(format("Connect url: {} exception.", url), e);
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }

    @Override
    public boolean close() {
        if (this.closed.compareAndSet(false, true)) {
            this.clients.forEach(Client::close);
            workerGroup.shutdownGracefully();
            buses().closeEvent().removeListener(this.closeListener);
            return true;
        }
        return false;
    }

}