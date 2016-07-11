package com.tny.game.net.client.nio;

import com.tny.game.log.CoreLogger;
import com.tny.game.net.base.AppContext;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.dispatcher.MessageHandler;
import com.tny.game.net.dispatcher.RequestSession;
import com.tny.game.net.dispatcher.SessionHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NetConnecter {

    protected static final Logger LOG = LoggerFactory.getLogger(CoreLogger.CLIENT);

    private Bootstrap bootstrap = null;

    private MessageHandler messageHandler;

    private ChannelMaker<Channel> channelMaker;

    private AppContext appContext;

    public NetConnecter(AppContext appContext) {
        this.bootstrap = new Bootstrap();
        this.channelMaker = appContext.getChannelMaker();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.messageHandler = new MessageHandler();
        this.messageHandler.setAppContext(appContext);
        this.bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        NetConnecter.this.channelMaker.initChannel(ch);
                        ch.pipeline().addLast("gameHandler", NetConnecter.this.messageHandler);
                    }
                });
        this.addShutdownHook(workerGroup);
    }

    public RequestSession connect(String host, int port) throws IOException {
        return connect(host, port, null);
    }

    public RequestSession connect(String host, int port, ConnectedCallback callback) throws IOException {
        NetClient client = new NetClient(this.bootstrap, host, port, callback);
        client.connect();
        return client;
    }

    public RequestSession awaitConnect(String host, int port) throws IOException, InterruptedException {
        return awaitConnect(host, port, null);
    }

    public RequestSession awaitConnect(String host, int port, ConnectedCallback callback) throws IOException, InterruptedException {
        NetClient client = new NetClient(this.bootstrap, host, port, callback);
        client.awaitConnect();
        return client;
    }

    public RequestSession awaitConnect(String host, int port, long timeoutMillis) throws IOException, InterruptedException {
        return awaitConnect(host, port, timeoutMillis, null);
    }

    public RequestSession awaitConnect(String host, int port, long timeoutMillis, ConnectedCallback callback) throws IOException, InterruptedException {
        NetClient client = new NetClient(this.bootstrap, host, port, callback);
        client.awaitConnect(timeoutMillis);
        return client;
    }

    private void addShutdownHook(final EventLoopGroup workerGroup) {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            {
                this.setName("ClientShutdownHookThread");
            }

            @Override
            public void run() {
                NetConnecter.LOG.info("#Connecter#正在关闭服务器......");
                SessionHolder sessionHolder = NetConnecter.this.appContext.getSessionHolder();
                sessionHolder.offlineAll();
                workerGroup.shutdownGracefully();
                NetConnecter.LOG.info("#Connecter#服务器已关闭!!!");
            }
        });
    }

}
