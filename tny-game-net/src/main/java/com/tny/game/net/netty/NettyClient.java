package com.tny.game.net.netty;

import com.tny.game.common.config.Config;
import com.tny.game.common.utils.URL;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.Client;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.exception.DispatchException;
import com.tny.game.net.exception.RemotingException;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.tunnel.Tunnels;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;


public class NettyClient<UID> extends NettyApp implements Client<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private NettyAppConfiguration appConfiguration;

    private Set<Channel> channels = new CopyOnWriteArraySet<>();

    private AtomicBoolean close = new AtomicBoolean(false);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(workerGroup::shutdownGracefully) {
            {
                this.setName("ClientShutdownHookThread");
            }
        });
    }

    @Sharable
    private class RemoveTunnelHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            channels.remove(ctx.channel());
            ctx.fireChannelInactive();
        }
    }

    public NettyClient(String name, NettyAppConfiguration appConfiguration) {
        super(name);
        this.appConfiguration = appConfiguration;
    }

    private Bootstrap bootstrap() {
        if (this.bootstrap != null)
            return this.bootstrap;
        synchronized (this) {
            if (this.bootstrap != null)
                return this.bootstrap;
            this.bootstrap = new Bootstrap();
            NettyMessageHandler messageHandler = new NettyMessageHandler(appConfiguration);
            ChannelInboundHandler channelRemoveHandler = new RemoveTunnelHandler();
            ChannelMaker<Channel> channelMaker = appConfiguration.getChannelMaker();
            this.bootstrap.group(workerGroup)
                    .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            if (channelMaker != null)
                                channelMaker.initChannel(ch);
                            ch.pipeline().addLast("nettyMessageHandler", messageHandler);
                            ch.pipeline().addLast("nettyChannelRemoveHandler", channelRemoveHandler);
                        }

                    });
            return this.bootstrap;
        }

    }

    public Tunnel<UID> connect(URL url, BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator) throws InterruptedException, ExecutionException, TimeoutException, DispatchException {
        if (this.isClosed())
            throw new RemotingException("client is closed");
        NettyClientTunnel<UID> tunnel = doConnect(null, url, loginContentCreator);
        if (tunnel != null)
            tunnel.login();
        return tunnel;
    }

    public Tunnel<UID> connect(URL url, MessageContent<UID> content) throws InterruptedException, ExecutionException, TimeoutException, DispatchException {
        return connect(url, (relogin, tunnel) -> content);
    }

    void reconnect(NettyClientTunnel<UID> tunnel) {
        if (this.isClosed())
            throw new RemotingException("client is closed");
        doConnect(tunnel, tunnel.getUrl(), tunnel.getLoginContentCreator());
    }

    private long getConnectTimeout(URL url) {
        Config config = appConfiguration.getProperties();
        return url.getParameter(AppConstants.CONNECT_TIMEOUT_URL_PARAM, config.getLong(AppConstants.CONNECT_TIMEOUT_URL_PARAM, 5000L));
    }

    @SuppressWarnings("unchecked")
    private NettyClientTunnel<UID> doConnect(NettyClientTunnel<UID> reconnectTunnel, URL url, BiFunction<Boolean, Tunnel<UID>, MessageContent<UID>> loginContentCreator) {
        if (reconnectTunnel != null && reconnectTunnel.isConnected())
            return reconnectTunnel;
        ChannelFuture channelFuture = this.bootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
        try {
            long connectTimeout = getConnectTimeout(url);
            channelFuture.get(connectTimeout, TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess()) {
                Channel channel = channelFuture.channel();
                NettyClientTunnel<UID> tunnel = reconnectTunnel;
                if (tunnel == null) {
                    tunnel = new NettyClientTunnel<>(url, channel, appConfiguration, loginContentCreator);
                    Tunnels.put(tunnel);
                }
                channel.attr(NettyAttrKeys.TUNNEL).set(tunnel);
                channel.attr(NettyAttrKeys.CLIENT).set(NettyClient.this);
                channels.add(channel);
                if (reconnectTunnel != null)
                    reconnectTunnel.resetChannel(channel);
                return tunnel;
            } else {
                return null;
            }
        } catch (Throwable e) {
            throw new RemotingException(e);
        }
    }


    // Channel reconnect(NettyClientTunnel<UID> tunnel) {
    //     synchronized (this) {
    //         URL url = tunnel.getUrl();
    //         ChannelFuture channelFuture = this.bootstrap.connect(new InetSocketAddress(url.getHost(), url.getPort()));
    //         try {
    //             boolean result = channelFuture.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
    //             if (result && channelFuture.isSuccess()) {
    //                 Channel newChannel = channelFuture.channel();
    //                 NettyClientTunnel<UID> tunnel = newChannel.attr(NETTY_TUNNEL).get();
    //                 tunnel.setUrl(url)
    //                         .setLoginContentCreator(loginContentCreator);
    //                 MessageFuture loginFuture = tunnel.login();
    //                 loginFuture.get(loginTimeout, TimeUnit.MILLISECONDS);
    //                 this.session = tunnel.getSession();
    //                 return this.session;
    //             }
    //         } catch (Throwable e) {
    //             throw new RemotingException(e);
    //         }
    //         return null;
    //     }
    // }

    // @Override
    // public boolean isConnected() {
    //     if (this.session == null)
    //         return false;
    //     Tunnel<UID> tunnel = this.session.getCurrentTunnel();
    //     return tunnel != null && tunnel.isConnected();
    // }


    // @Override
    // public boolean close() {
    //     this.close.set(true);
    //     // return getAndCheckSession().close();
    // }

    @Override
    public boolean isClosed() {
        return this.close.get();
    }

    @Override
    public boolean close() {
        if (this.close.compareAndSet(false, true)) {
            this.channels.forEach(ChannelOutboundInvoker::close);
            return true;
        }
        return false;
    }

    // @Override
    // public boolean isClosed() {
    //     return !isConnected();
    // }

    // private Session<UID> getAndCheckSession() {
    //     Session<UID> session = this.session;
    //     if (session == null)
    //         throw new RemotingException(this.getName() + " [" + this.connectAddress + "] session is null");
    //     if (!isConnected())
    //         return doConnect();
    //     return session;
    // }

}
