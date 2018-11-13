package com.tny.game.net.netty4;

import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.listener.ClientCloseListener;
import com.tny.game.net.exception.NetException;
import com.tny.game.net.transport.Certificate;
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

public class NettyClientGuide extends NettyBootstrap implements ClientGuide {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private Map<String, NettyClient<?>> clients = new ConcurrentHashMap<>();

    private AtomicBoolean close = new AtomicBoolean(false);

    private EndpointKeeperManager endpointKeeperManager;

    private ClientCloseListener<?> closeListener = (client) -> clients.remove(clientKey(client.getUrl()), client);

    public NettyClientGuide(NettyClientUnitSetting clientSetting) {
        this(clientSetting, null);
    }

    public NettyClientGuide(NettyClientUnitSetting clientSetting, EndpointKeeperManager endpointKeeperManager) {
        super(clientSetting);
        this.endpointKeeperManager = endpointKeeperManager;
        buses().closeEvent().addListener(closeListener);
    }

    private String clientKey(URL url) {
        return url.getHost() + ":" + url.getPort();
    }

    @Override
    public <UID> Client<UID> connect(URL url, Certificate<UID> certificate, PostConnect<UID> connect) {
        NettyClient<UID> client = new NettyClient<>(this, url, certificate, connect);
        NettyClient<UID> old = as(clients.putIfAbsent(clientKey(url), client));
        if (old != null) {
            return old;
        } else {
            client.open();
            ClientKeeper<UID> keeper = this.endpointKeeperManager.loadOcCreate(client.getUserType(), EndpointType.CLIENT);
            keeper.register(client);
            return client;
        }
    }

    private Bootstrap getBootstrap() {
        if (this.bootstrap != null)
            return this.bootstrap;
        synchronized (this) {
            if (this.bootstrap != null)
                return this.bootstrap;
            this.bootstrap = new Bootstrap();
            NettyMessageHandler messageHandler = new NettyMessageHandler(this.messageHandler);
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
                        }

                    });
            return this.bootstrap;
        }

    }

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
            this.clients.values().forEach(NettyClient::close);
            workerGroup.shutdownGracefully();
            buses().closeEvent().removeListener(closeListener);
            return true;
        }
        return false;
    }

    public NettyClientGuide setEndpointKeeperManager(EndpointKeeperManager endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
        return this;
    }

}
