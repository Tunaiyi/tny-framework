package com.tny.game.flash;

import com.tny.game.log.CoreLogger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class FlashPolicyServer {

    private static final Logger LOG = LoggerFactory.getLogger(CoreLogger.NET);


    private EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("FlashPolicy-NIO-Boss-EventLoop#"));
    private EventLoopGroup childGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("FlashPolicy-NIO-Child-EventLoop#"));

    public void start(final InetSocketAddress address) {
        start(address.getAddress().getHostAddress(), address.getPort());
    }

    public void start(final String host, int port) {
        LOG.info("FlashPolicyServer server starting " + host + ":" + port);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {

                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("frameDecoder",
                                new DelimiterBasedFrameDecoder(1024, Delimiters.nulDelimiter()));
                        pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                        pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
                        pipeline.addLast("handler", new FlashPolicyHandler());

                    }

                })
                .option(ChannelOption.SO_REUSEADDR, false)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(host, port)).awaitUninterruptibly();
        if (!channelFuture.isSuccess()) {
            throw new RuntimeException(channelFuture.cause());
        }
        LOG.info("FlashPolicyServer started " + host + ":" + port);
    }
}
