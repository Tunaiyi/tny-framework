package com.tny.game.net.netty4;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.collection.CopyOnWriteMap;
import com.tny.game.common.config.Config;
import com.tny.game.net.base.*;
import com.tny.game.net.base.listener.ServerClosedListener;
import com.tny.game.net.command.DefaultMessageHandler;
import com.tny.game.net.transport.*;
import com.tny.game.net.utils.NetConfigs;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.tny.game.common.utils.ObjectAide.*;

public class NettyServerBootstrap extends NettyBootstrap implements ServerBootstrap {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup parentGroup = createLoopGroup(EPOLL, 1, "Sever-Boss-LoopGroup");

    private static final EventLoopGroup childGroup = createLoopGroup(EPOLL, Runtime.getRuntime().availableProcessors() * 2, "Sever-Child-LoopGroup");

    private volatile io.netty.bootstrap.ServerBootstrap bootstrap;

    private volatile NettyAppConfiguration<Object> appConfiguration;

    private Collection<InetSocketAddress> bindAddresses;

    private Map<String, Channel> channels = new CopyOnWriteMap<>();

    /**
     * 服务器关闭监听器
     */
    private List<ServerClosedListener> listenerList = new CopyOnWriteArrayList<>();

    private io.netty.bootstrap.ServerBootstrap bootstrap() {
        if (this.bootstrap != null)
            return this.bootstrap;
        synchronized (this) {
            if (this.bootstrap != null)
                return this.bootstrap;
            this.bootstrap = new io.netty.bootstrap.ServerBootstrap();
            NettyMessageHandler messageHandler = new NettyMessageHandler(new DefaultMessageHandler(appConfiguration));
            this.bootstrap.group(parentGroup, childGroup)
                    .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            if (channelMaker != null)
                                channelMaker.initChannel(ch);
                            ch.pipeline() .addLast("nettyMessageHandler", messageHandler);
                            Certificate<Object> defaultCertificate = Certificates.createUnautherized(appConfiguration.getDefaultUserId());
                            NettyTunnel<Object> tunnel = new NettyServerTunnel<>(ch, defaultCertificate)
                                    .setMessageFactory(appConfiguration.getMessageFactory());
                            ch.attr(NettyAttrKeys.TUNNEL).set(tunnel);
                            tunnel.open();
                        }
                    });
            return this.bootstrap;
        }

    }

    public NettyServerBootstrap(ChannelMaker<Channel> channelMaker, NettyAppConfiguration<?> appConfiguration) {
        super(appConfiguration.getName(), channelMaker);
        this.appConfiguration = as(appConfiguration);
        Config properties = appConfiguration.getProperties();
        this.bindAddresses = properties.getObject(NetConfigs.SERVER_BIND_IPS);
        this.bindAddresses = ImmutableSet.copyOf(this.bindAddresses);

    }

    @Override
    public void open() {
        this.bindAddresses.forEach(this::bind);
    }

    private void bind(final InetSocketAddress address) {
        String addressString = toAddressString(address);
        Channel channel = channels.get(addressString);
        if (channel != null) {
            if (channel.close().awaitUninterruptibly(30000L)) {
                channels.remove(addressString, channel);
            }
        }
        LOG.info("#NettyServer#正在打开监听{}端口", address);
        ChannelFuture channelFuture = this.bootstrap().bind(address);
        if (channelFuture.awaitUninterruptibly(30000L)) {
            this.channels.put(addressString, channelFuture.channel());
            LOG.info("#NettyServer#{}端口已监听", address);
        } else {
            LOG.info("#NettyServer#{}端口监听失败", address);
        }
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean close() {
        LOG.info("#NettyServer#正在关闭服务器......");
        this.channels.forEach((address, channel) -> {
            try {
                NettyServerBootstrap.LOG.info("#NettyServer# Channel {} 关闭中......", channel);
                channel.disconnect();
                NettyServerBootstrap.LOG.info("#NettyServer# Channel {} 关闭完成", channel);
            } catch (Throwable e) {
                NettyServerBootstrap.LOG.error("#NettyServer# Channel {} 关闭异常!!!", channel, e);
                LOG.error("{} close exception", address, e);
            }
        });
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        NettyServerBootstrap.this.fireServerClosed();
        NettyServerBootstrap.LOG.info("#NettyServer#服务器已关闭!!!");
        return true;
    }

    @Override
    public void addClosedListener(final ServerClosedListener listener) {
        this.listenerList.add(listener);
    }

    @Override
    public void addClosedListeners(final Collection<ServerClosedListener> listenerCollection) {
        this.listenerList.addAll(listenerCollection);
    }

    @Override
    public void clearClosedListener() {
        this.listenerList.clear();
    }

    private void fireServerClosed() {
        for (ServerClosedListener listener : this.listenerList) {
            try {
                listener.onClosed(this);
            } catch (Exception e) {
                NettyServerBootstrap.LOG.error("#NettyServer#fireServerClosed异常!!!", e);
            }
        }
    }

    private String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}
