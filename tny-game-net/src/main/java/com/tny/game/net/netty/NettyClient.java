package com.tny.game.net.netty;

import com.tny.game.net.base.Client;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.exception.RemotingException;
import com.tny.game.net.message.Message;
import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.Protocol;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.session.ResendMessage;
import com.tny.game.net.tunnel.Tunnel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public class NettyClient<UID> extends NettyApp implements Client<UID> {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.CLIENT);

    private static final boolean EPOLL = isEpoll();

    private static EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

    private Bootstrap bootstrap = null;

    private InetSocketAddress connectAddress;

    private long connectTimeout;

    private long loginTimeout;

    private Tunnel<UID> tunnel;

    private Protocol loginProtocol;

    private Supplier<Object> loginBodyCreator;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workerGroup.shutdownGracefully()));
    }

    public NettyClient(String name, InetSocketAddress connectAddress, NettyAppConfiguration appConfiguration) {
        super(name);
        this.connectAddress = connectAddress;
        this.connectTimeout = 30000L;
        this.loginTimeout = 30000L;
        this.bootstrap = new Bootstrap();
        ChannelMaker<Channel> channelMaker = appConfiguration.getChannelMaker();
        NettyMessageHandler messageHandler = new NettyMessageHandler(appConfiguration);
        this.bootstrap.group(workerGroup)
                .channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
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
        // this.addShutdownHook(workerGroup);
    }

    public NettyClient<UID> setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public NettyClient<UID> setLoginTimeout(long loginTimeout) {
        this.loginTimeout = loginTimeout;
        return this;
    }


    public NettyClient<UID> setLoginBodyCreator(Protocol loginProtocol, Supplier<Object> loginBodyCreator) {
        this.loginProtocol = loginProtocol;
        this.loginBodyCreator = loginBodyCreator;
        return this;
    }

    @Override
    public void reconnect() {
        this.disconnect();
        this.doConnect();
    }

    @Override
    public void disconnect() {
        Tunnel<UID> tunnel = this.tunnel;
        if (tunnel != null)
            tunnel.close();
    }

    @SuppressWarnings("unchecked")
    private void doConnect() {
        // long startAt = System.currentTimeMillis();
        ChannelFuture channelFuture = this.bootstrap.connect(connectAddress);
        MessageFuture loginFuture = null;
        try {
            boolean result = channelFuture.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
            if (result && channelFuture.isSuccess()) {
                Channel newChannel = channelFuture.channel();
                Tunnel<UID> oldTunnel = this.tunnel;
                this.tunnel = newChannel.attr(NettyAttrKeys.TUNNEL).get();
                if (oldTunnel != null) {
                    oldTunnel.close();
                }
                MessageContent<?> loginMessage = MessageContent.toRequest(loginProtocol, loginBodyCreator.get())
                        .createMessageFuture(loginTimeout + 10000L);
                loginFuture = loginMessage.getMessageFuture();
                this.tunnel.send(loginMessage);
                loginFuture.get(loginTimeout, TimeUnit.MILLISECONDS);
            }
        } catch (Throwable e) {
            throw new RemotingException(e);
        } finally {
            if (!isConnected()) {
                if (channelFuture != null)
                    channelFuture.cancel(true);
                if (loginFuture != null)
                    loginFuture.cancel(true);
            }
        }
    }

    @Override
    public boolean isConnected() {
        Tunnel<UID> tunnel = this.tunnel;
        return tunnel != null && tunnel.isConnected();
    }

    @Override
    public UID getUID() {
        return null;
    }

    @Override
    public String getUserGroup() {
        return null;
    }

    @Override
    public void send(MessageContent<?> content) {
        Tunnel<UID> tunnel = getAndCheckTunnel();
        if (tunnel.isClosed())
            this.doConnect();
        if (tunnel.isConnected())
            this.tunnel.send(content);
    }

    @Override
    public void receive(Message<UID> message) {
        getAndCheckTunnel().receive(message);
    }

    @Override
    public void resend(ResendMessage message) {
        getAndCheckTunnel().resend(message);
    }

    @Override
    public boolean close() {
        return getAndCheckTunnel().close();
    }

    @Override
    public boolean isClosed() {
        return !isConnected();
    }

    private Tunnel<UID> getAndCheckTunnel() {
        Tunnel<UID> tunnel = this.tunnel;
        if (tunnel == null)
            throw new RemotingException(this.getName() + " [" + this.connectAddress + "] tunnel is null");
        return tunnel;
    }

}
