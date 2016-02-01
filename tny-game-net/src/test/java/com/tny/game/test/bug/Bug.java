package com.tny.game.test.bug;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bug {

    private static class ClientHandler extends SimpleChannelInboundHandler<Object> {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        boolean close = false;

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
            final Channel channel = ctx.channel();
            if (!close) {
                executorService.schedule(() -> {
                    if (channel.isActive())
                        channel.close();
                }, 5, TimeUnit.MINUTES);
            }
        }
    }

    private static class ServerHandler extends SimpleChannelInboundHandler<Object> {

        int port = 0;

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.channel().write("server(" + port + ") : " + msg + "\r\n");
        }

    }

    private static class Server {

        public void serve(final int port) {

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup childGroup = new NioEventLoopGroup();
            final ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_REUSEADDR, false)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                            pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
                            ServerHandler handler = new ServerHandler();
                            handler.port = port;
                            pipeline.addLast("handler", handler);
                        }

                    });
            bootstrap.bind(new InetSocketAddress(port));
        }
    }

    public static class Client {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup(1);
        final Bootstrap bootstrap = new Bootstrap();
        final List<Channel> channelList = new ArrayList<>();

        public Client() {
            bootstrap.handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("decoder", new StringDecoder(Charset.forName("UTF-8")));
                    pipeline.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
                    pipeline.addLast("handler", new ClientHandler());
                }

            })
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true);
        }

        public void connect(final int port) {
            for (int i = 0; i < 100; i++) {
                final int index = i;
                ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", port));
                Channel channel = future.awaitUninterruptibly().channel();
                if (channel != null) {
                    channelList.add(channel);
                    new Thread(() -> {
                        for (int number = 0; number < 100000; number++)
                            channel.write(Unpooled.wrappedBuffer((index + " -- " + number + "\t +abcedefgheijklmnopqrstuvexyzABCDEFGHIJKLMNOPQRSTUVWXYZ+\r\n")
                                    .getBytes())).awaitUninterruptibly();
                    }).start();
                }
            }
        }

        public void close() {
            for (Channel channel : channelList) {
                channel.close().awaitUninterruptibly();
            }
            childGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Server().serve(8080);
        Thread.sleep(10000000000000l);
        // new Server().serve(8081);
        // }
        System.exit(0);
    }
}