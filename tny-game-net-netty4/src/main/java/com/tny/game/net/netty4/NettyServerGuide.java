package com.tny.game.net.netty4;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.collection.*;
import com.tny.game.common.event.*;
import com.tny.game.net.base.*;
import com.tny.game.net.base.listener.*;
import com.tny.game.net.transport.*;
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
import java.util.Map;

import static com.tny.game.common.utils.ObjectAide.*;

public class NettyServerGuide extends NettyBootstrap implements ServerGuide {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private static final boolean EPOLL = isEpoll();

    private static final EventLoopGroup parentGroup = createLoopGroup(EPOLL, 1, "Sever-Boss-LoopGroup");

    private static final EventLoopGroup childGroup = createLoopGroup(EPOLL, Runtime.getRuntime().availableProcessors() * 2, "Sever-Child-LoopGroup");

    private volatile ServerBootstrap bootstrap;

    private volatile ServerBootstrapSetting serverUnitSetting;

    private Collection<InetSocketAddress> bindAddresses;

    private Map<String, Channel> channels = new CopyOnWriteMap<>();

    /**
     * 服务器关闭监听器
     */
    private BindVoidEventBus<ServerClosedListener, ServerGuide> onClose = EventBuses.of(
            ServerClosedListener.class, ServerClosedListener::onClosed);

    public NettyServerGuide(NettyServerBootstrapSetting unitSetting) {
        super(unitSetting);
        this.serverUnitSetting = as(unitSetting);
        this.bindAddresses = ImmutableSet.copyOf(this.serverUnitSetting.getBindAddresses());
    }

    @Override
    public void open() {
        this.bindAddresses.forEach(this::bind);
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean close() {
        LOG.info("#NettyServer [ {} ] | 正在关闭服务器......", serverUnitSetting.getName());
        this.channels.forEach((address, channel) -> {
            try {
                NettyServerGuide.LOG.info("#NettyServer [ {} ] | Channel {} 关闭中......", serverUnitSetting.getName(), channel);
                channel.disconnect();
                NettyServerGuide.LOG.info("#NettyServer [ {} ] | Channel {} 关闭完成", serverUnitSetting.getName(), channel);
            } catch (Throwable e) {
                NettyServerGuide.LOG.error("#NettyServer [ {} ] | Channel {} 关闭异常!!!", serverUnitSetting.getName(), channel, e);
                LOG.error("NettyServer [ {} ] | {} close exception", serverUnitSetting.getName(), address, e);
            }
        });
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        NettyServerGuide.this.fireServerClosed();
        NettyServerGuide.LOG.info("#NettyServer [ {} ] | 服务器已关闭!!!", serverUnitSetting.getName());
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
        Channel channel = channels.get(addressString);
        if (channel != null) {
            if (channel.close().awaitUninterruptibly(30000L)) {
                channels.remove(addressString, channel);
            }
        }
        LOG.info("#NettyServer [ {} ] | 正在打开监听{}端口", this.serverUnitSetting.getName(), address);
        ChannelFuture channelFuture = this.bootstrap().bind(address);
        if (channelFuture.awaitUninterruptibly(30000L)) {
            this.channels.put(addressString, channelFuture.channel());
            LOG.info("#NettyServer [ {} ] | {}端口已监听", this.serverUnitSetting.getName(), address);
        } else {
            LOG.info("#NettyServer [ {} ] | {}端口监听失败", this.serverUnitSetting.getName(), address);
        }
    }

    private ServerBootstrap bootstrap() {
        if (this.bootstrap != null)
            return this.bootstrap;
        synchronized (this) {
            if (this.bootstrap != null)
                return this.bootstrap;
            this.bootstrap = new io.netty.bootstrap.ServerBootstrap();
            NettyMessageHandler nettyMessageHandler = new NettyMessageHandler();
            this.bootstrap.group(parentGroup, childGroup);
            this.bootstrap.channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
            this.bootstrap.option(ChannelOption.SO_REUSEADDR, true);
            this.bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            this.bootstrap.childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    if (channelMaker != null)
                        channelMaker.initChannel(ch);
                    ch.pipeline().addLast("nettyMessageHandler", nettyMessageHandler);
                    Certificate<Object> defaultCertificate = Certificates.createUnautherized();
                    NettyTunnel<Object, ?> tunnel = new NettyServerTunnel<>(ch, defaultCertificate,
                            getEventHandler(), messageFactory);
                    ch.attr(NettyAttrKeys.TUNNEL).set(tunnel);
                    tunnel.open();
                }
            });
            return this.bootstrap;
        }

    }

}
