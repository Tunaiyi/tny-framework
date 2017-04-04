package com.tny.game.net.netty;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.config.Config;
import com.tny.game.common.utils.collection.CopyOnWriteMap;
import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.base.AppConstants;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.Server;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.listener.SeverClosedListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class NettyServer extends NettyApp implements Server {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup parentGroup = createLoopGroup(EPOLL, 1, "Sever-Boss-LoopGroup");

    private static final EventLoopGroup childGroup = createLoopGroup(EPOLL, Runtime.getRuntime().availableProcessors() * 2, "Sever-Child-LoopGroup");

    private ServerBootstrap bootstrap;

    private Collection<InetSocketAddress> bindAddresses;

    private Map<String, Channel> channels = new CopyOnWriteMap<>();

    /**
     * 服务器关闭监听器
     */
    private List<SeverClosedListener> listenerList = new CopyOnWriteArrayList<>();

    public NettyServer(NettyAppConfiguration appConfiguration) {
        this(appConfiguration.getChannelMaker(), appConfiguration);
    }


    public NettyServer(ChannelMaker<Channel> channelMaker, AppConfiguration appConfiguration) {
        super(appConfiguration.getName());
        Config properties = appConfiguration.getProperties();
        this.bootstrap = new ServerBootstrap();
        this.bindAddresses = properties.getObject(AppConstants.SERVER_BIND_IPS);
        this.bindAddresses = ImmutableSet.copyOf(this.bindAddresses);
        NettyMessageHandler messageHandler = new NettyMessageHandler(appConfiguration);
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
                        ch.pipeline().addLast("nettyMessageHandler", messageHandler);
                    }
                });
        this.addShutdownHook(parentGroup, childGroup);
    }

    @Override
    public void open() {
        this.bindAddresses.forEach(this::bind);
    }

    private void bind(final InetSocketAddress address) {
        Channel channel = channels.get(address);
        String addressString = toAddressString(address);
        if (channel != null) {
            if (channel.close().awaitUninterruptibly(30000L)) {
                channels.remove(addressString, channel);
            }
        }
        LOG.info("#NettyServer#正在打开监听{}端口", address);
        ChannelFuture channelFuture = this.bootstrap.bind(address);
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
                NettyServer.LOG.info("#NettyServer# Channel {} 关闭中......", channel);
                channel.disconnect().awaitUninterruptibly();
                NettyServer.LOG.info("#NettyServer# Channel {} 关闭完成", channel);
            } catch (Throwable e) {
                NettyServer.LOG.error("#NettyServer# Channel {} 关闭异常!!!", channel, e);
                LOG.error("{} close exception", address, e);
            }
        });
        NettyServer.this.fireServerClosed();
        NettyServer.LOG.info("#NettyServer#服务器已关闭!!!");
        return true;
    }

    @Override
    public void addClosedListener(final SeverClosedListener listener) {
        this.listenerList.add(listener);
    }

    @Override
    public void addClosedListeners(final Collection<SeverClosedListener> listenerCollection) {
        this.listenerList.addAll(listenerCollection);
    }

    @Override
    public void clearClosedListener() {
        this.listenerList.clear();
    }

    private void addShutdownHook(final EventLoopGroup boss, final EventLoopGroup child) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

        }) {

            {
                this.setName("ServerShutdownHookThread");
            }

        });
    }

    private void fireServerClosed() {
        for (SeverClosedListener listener : this.listenerList) {
            try {
                listener.onClosed(this);
            } catch (Exception e) {
                NettyServer.LOG.error("#NettyServer#fireServerClosed异常!!!", e);
            }
        }
    }

    private String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}
