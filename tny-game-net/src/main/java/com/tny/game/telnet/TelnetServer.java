package com.tny.game.telnet;

import com.tny.game.log.NetLogger;
import com.tny.game.net.base.NetServerAppContext;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.listener.ServerClosedListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TelnetServer implements ServerClosedListener {

    public static final String HEAD_KEY = "tny.server.";

    private static final String TELNET_ENABLE = HEAD_KEY + "telnet.enable";
    private static final String TELNET_HOST = HEAD_KEY + "telnet.host";
    private static final String TELNET_PORT = HEAD_KEY + "telnet.port";

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private TelnetHandler telnetHandler;

    private ChannelInitializer<Channel> channelInitializer;

    private ServerConfig serverContext;

    private Channel channel;

    public TelnetServer(NetServerAppContext appContext, TelnetHandler telnetHandler) {
        this.telnetHandler = telnetHandler;
        this.serverContext = appContext.getServerConfig();
        this.channelInitializer = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());
                pipeline.addLast("handle", TelnetServer.this.telnetHandler);
            }
        };
    }

    private void bind(final InetSocketAddress address) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("Telnet-Boss-NIO-EventLoop#"));
        EventLoopGroup childGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("Telnet-Child-NIO-EventLoop#"));
        bootstrap.group(bossGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.channelInitializer)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture channelFuture = bootstrap.bind(address).awaitUninterruptibly();
        if (channelFuture.isSuccess()) {
            this.channel = channelFuture.channel();
        } else {
            throw new RuntimeException(channelFuture.cause());
        }
    }

    public void start() {
        LOG.info("#TelnetServer#启动服务器");
        String telnetEnable = this.serverContext.getConfig().getStr(TELNET_ENABLE);
        if (telnetEnable == null || !Boolean.parseBoolean(telnetEnable))
            return;
        String hostname = this.serverContext.getConfig().getStr(TELNET_HOST);
        if (hostname == null)
            hostname = "0.0.0.0";
        String port = this.serverContext.getConfig().getStr(TELNET_PORT);
        if (port == null)
            port = "23";
        InetSocketAddress address = new InetSocketAddress(hostname, Integer.parseInt(port));
        LOG.info("#TelnetServer#正在打开监听{}端口", address);
        this.bind(address);
        LOG.info("#TelnetServer#服务器已启动");
    }

    @Override
    public void onServerClosed() {
        if (this.channel != null)
            this.channel.disconnect();
    }
}
