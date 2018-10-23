package com.tny.game.net.netty4;

import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.command.DefaultMessageHandler;
import com.tny.game.net.exception.NetException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.tny.game.common.utils.StringAide.*;

public class NettyClientBootstrap<UID> extends NettyBootstrap implements ClientBootstrap<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private NettyAppConfiguration appConfiguration;

    private Set<NettyClient<?>> clients = new CopyOnWriteArraySet<>();

    private AtomicBoolean close = new AtomicBoolean(false);

    @Sharable
    private class RemoveTunnelHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            ctx.fireChannelInactive();
        }

    }

    public NettyClientBootstrap(String name, ChannelMaker<Channel> channelMaker, NettyAppConfiguration appConfiguration) {
        super(name, channelMaker);
        this.appConfiguration = appConfiguration;
    }

    Bootstrap getBootstrap() {
        if (this.bootstrap != null)
            return this.bootstrap;
        synchronized (this) {
            if (this.bootstrap != null)
                return this.bootstrap;
            this.bootstrap = new Bootstrap();
            NettyMessageHandler messageHandler = new NettyMessageHandler(new DefaultMessageHandler(appConfiguration));
            ChannelInboundHandler channelRemoveHandler = new RemoveTunnelHandler();
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

    // public Tunnel<UID> connect(URL url) throws InterruptedException, ExecutionException, TimeoutException, TunnelWriteException {
    //     if (this.isClosed())
    //         throw new RemotingException("client is closed");
    //     NettyClientTunnel<UID> tunnel = null;
    //     try {
    //         tunnel = connectTunnel(null, url);
    //         if (tunnel != null)
    //             tunnel.login();
    //     } catch (Throwable e) {
    //         if (tunnel != null)
    //             tunnel.close();
    //         throw e;
    //     }
    //     return tunnel;
    // }

    @SuppressWarnings("unchecked")
    Channel connect(URL url, long connectTimeout) throws NetException {
        Throws.checkNotNull(url, "url is null");
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
                throw new NetException(format("Connect url: {} failed. result: {} success: {}, connected: {}", url, result, success, connected), channelFuture.cause());
            } else {
                channelFuture.cancel(true);
                throw new NetException(format("Connect url: {} timeout. result: {}, success: {}, connected: {}", url, result, success, connected), channelFuture.cause());
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
        return this.close.get();
    }

    @Override
    public boolean close() {
        if (this.close.compareAndSet(false, true)) {
            this.clients.forEach(NettyClient::close);
            workerGroup.shutdownGracefully();
            return true;
        }
        return false;
    }


}
