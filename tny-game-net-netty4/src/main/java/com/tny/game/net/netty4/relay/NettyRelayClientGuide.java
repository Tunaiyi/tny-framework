/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.netty4.relay;

import com.tny.game.common.lifecycle.unit.*;
import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.endpoint.listener.ClientEventBuses.*;

public class NettyRelayClientGuide extends NettyBootstrap<NettyRelayClientBootstrapSetting> implements RelayClientGuide {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private final Map<String, NettyClient<?>> clients = new ConcurrentHashMap<>();

    private final AtomicBoolean closed = new AtomicBoolean(false);

    private RemoteRelayExplorer localRelayExplorer;

    private final ClientCloseListener<Object> closeListener = (client) -> this.clients.remove(clientKey(client.getUrl()),
            as(client, NettyClient.class));

    public NettyRelayClientGuide(NetAppContext appContext, NettyRelayClientBootstrapSetting clientSetting) {
        super(appContext, clientSetting);
        buses().<Object>closeEvent().addListener(this.closeListener);
    }

    public NettyRelayClientGuide(NetAppContext appContext, NettyRelayClientBootstrapSetting clientSetting, ChannelMaker<Channel> channelMaker) {
        super(appContext, clientSetting, channelMaker);
        buses().<Object>closeEvent().addListener(this.closeListener);
    }

    private String clientKey(URL url) {
        return url.getHost() + ":" + url.getPort();
    }

    @Override
    public void connect(URL url, long timeout, RelayConnectCallback callback) {
        Asserts.checkNotNull(url, "url is null");
        ChannelFuture channelFuture = this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                callback.complete(true, url, createNetRelayLink(channelFuture.channel()), null);
            } else {
                callback.complete(false, url, null, future.cause());
            }
        });
    }

    @Override
    public RelayTransporter connect(URL url, long connectTimeout) throws NetException {
        Asserts.checkNotNull(url, "url is null");
        ChannelFuture channelFuture = null;
        try {
            channelFuture = this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
            boolean result = channelFuture.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
            boolean success = channelFuture.isSuccess();
            if (result && success) {
                return createNetRelayLink(channelFuture.channel());
            }
            if (channelFuture.cause() != null) {
                throw new NetException(format("Connect url: {} failed. result: {} success: {}", url, result, success),
                        channelFuture.cause());
            } else {
                throw new NetException(format("Connect url: {} timeout. result: {}, success: {}", url, result, success),
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
    protected void onLoadUnit(NettyRelayClientBootstrapSetting setting) {
        this.localRelayExplorer = UnitLoader.getLoader(RemoteRelayExplorer.class).checkUnit();
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
            RelayPacketProcessor relayPacketProcessor = new RemoteRelayPacketProcessor(this.localRelayExplorer);
            NettyRelayPacketHandler relayMessageHandler = new NettyRelayPacketHandler(relayPacketProcessor);
            this.bootstrap.group(workerGroup)
                    .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel channel) throws Exception {
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
            this.clients.values().forEach(NettyClient::close);
            workerGroup.shutdownGracefully();
            buses().closeEvent().removeListener(this.closeListener);
            return true;
        }
        return false;
    }

    private RelayTransporter createNetRelayLink(Channel channel) {
        return new NettyChannelRelayTransporter(channel, this.getContext());
    }

}