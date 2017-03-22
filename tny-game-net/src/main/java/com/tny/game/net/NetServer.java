package com.tny.game.net;

import com.tny.game.flash.FlashPolicyServer;
import com.tny.game.net.base.NetLogger;
import com.tny.game.net.base.NetServerAppContext;
import com.tny.game.net.coder.ChannelMaker;
import com.tny.game.net.config.ServerConfig;
import com.tny.game.net.netty.NettyMessageHandler;
import com.tny.game.net.netty.NettyAttrKeys;
import com.tny.game.net.listener.ServerClosedListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetServer {

    protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.NET);

    private static final String FLASH_POLICY_USABLE = "tny.server.flash.policy.usable";
    private static final String FLASH_POLICY_PORT = "tny.server.flash.policy.port";

    private String name;

    private ServerConfig serverConfig;

    private ServerBootstrap bootstrap;

    private NettyMessageHandler messageHandler;

    private ChannelMaker<Channel> channelMaker;

    private NetServerAppContext appContext;

    private List<Channel> serverChannels = new ArrayList<>();

    /**
     * 服务器关闭监听器
     */
    private List<ServerClosedListener> listenerList = new CopyOnWriteArrayList<>();

    public NetServer() {
    }

    public NetServer(NetServerAppContext appContext) {
        this(null, appContext);
    }

    public NetServer(String name, NetServerAppContext appContext) {
        this(name, appContext, new NettyMessageHandler());
    }

    public NetServer(String name, NetServerAppContext appContext, NettyMessageHandler handler) {
        super();
        this.name = name;
        this.appContext = appContext;
        this.messageHandler = handler;//new SimpleMessageHandler();
    }

    private void addShutdownHook(final EventLoopGroup boss, final EventLoopGroup child) {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            {
                this.setName("ServerShutdownHookThread");
            }

            @Override
            public void run() {
                NetServer.LOG.info("#GameServer#正在关闭服务器......");
                List<Channel> serverChannel = NetServer.this.serverChannels;
                if (serverChannel != null) {
                    try {
                        serverChannel.forEach(Channel::disconnect);
                    } catch (Exception e) {
                        NetServer.LOG.error("#GameServer#serverChannel关闭异常!!!", e);
                    }
                }
                NetServer.this.fireServerClosed();
                boss.shutdownGracefully();
                child.shutdownGracefully();
                NetServer.LOG.info("#GameServer#服务器已关闭!!!");
            }
        });
    }

    private boolean isEpoll() {
        String osname = System.getProperties().getProperty("os.name");
        String osversion = System.getProperties().getProperty("os.version");
        if ("Linux".equals(osname)) {
            String[] vers = osversion.split("\\.", 0);
            if (vers.length >= 2) {
                try {
                    int major = Integer.parseInt(vers[0]);
                    int minor = Integer.parseInt(vers[1]);
                    if (major > 2 || (major == 2 && minor >= 6)) {
                        return true;
                    }
                } catch (NumberFormatException x) {
                    // format not recognized
                }
            }
        }
        return false;
    }

    private EventLoopGroup[] initLoopGroup(boolean epoll) {
        EventLoopGroup[] groups = new EventLoopGroup[2];
        if (epoll) {
            groups[0] = new EpollEventLoopGroup(1, new DefaultThreadFactory("Sever-Boss-NIO-EpollEventLoop#"));
            groups[1] = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, new DefaultThreadFactory("Sever-Child-NIO-EpollEventLoop#"));
        } else {
            groups[0] = new NioEventLoopGroup(1, new DefaultThreadFactory("Server-Boss-NIO-EventLoop#"));
            groups[1] = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2, new DefaultThreadFactory("Server-Child-NIO-EventLoop#"));
        }
        return groups;
    }

    public String getName() {
        return this.name;
    }

    private void bind(ServerBootstrap bootstrap, final InetSocketAddress address) {

        ChannelFuture channelFuture = bootstrap.bind(address).awaitUninterruptibly();
        if (channelFuture.isSuccess()) {
            this.serverChannels.add(channelFuture.channel());
        } else {
            throw new RuntimeException(channelFuture.cause());
        }
    }


    public void start() {
        NetServer.LOG.info("#GameServer#启动服务器");
        this.appContext.initContext((context) -> {
            this.messageHandler.setAppContext(appContext);
            this.serverConfig = appContext.getServerConfig();
            this.channelMaker = appContext.getChannelMaker();
            this.bootstrap = new ServerBootstrap();
            boolean epoll = this.isEpoll();
            final EventLoopGroup[] groups = this.initLoopGroup(epoll);
            this.bootstrap.group(groups[0], groups[1])
                    .channel(epoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            NetServer.this.channelMaker.initChannel(ch);
                            ch.pipeline().addLast("gameHandler", NetServer.this.messageHandler);
                            ch.attr(NettyAttrKeys.CONTEXT)
                                    .set(NetServer.this.appContext);
                        }
                    });
            this.addShutdownHook(groups[0], groups[1]);
        });
        Set<String> serverHost = new HashSet<>();
        for (InetSocketAddress address : this.serverConfig.getBindInetSocketAddressList(this.name)) {
            NetServer.LOG.info("#GameServer#正在打开监听{}端口", address);
            this.bind(this.bootstrap, address);
            serverHost.add(address.getAddress().getHostAddress());
            NetServer.LOG.info("#GameServer#{}端口已监听", address);
        }

        String policyUsable = this.serverConfig.getConfig().getStr(NetServer.FLASH_POLICY_USABLE);
        if (policyUsable != null && policyUsable.equals("true")) {
            for (String host : serverHost) {
                String port = this.serverConfig.getConfig().getStr(NetServer.FLASH_POLICY_PORT);
                new FlashPolicyServer().start(host, Integer.parseInt(port == null ? "843" : port));
            }
        }
        NetServer.LOG.info("#GameServer#服务器已启动");
    }

    public void addServerClosedListener(final ServerClosedListener listener) {
        this.listenerList.add(listener);
    }

    public void addServerClosedListeners(final Collection<ServerClosedListener> listenerCollection) {
        this.listenerList.addAll(listenerCollection);
    }

    public void clearServerClosedListener() {
        this.listenerList.clear();
    }

    public ServerConfig getServerContext() {
        return this.serverConfig;
    }

    private void fireServerClosed() {
        for (ServerClosedListener listener : this.listenerList) {
            try {
                listener.onServerClosed();
            } catch (Exception e) {
                NetServer.LOG.error("#GameServer#fireServerClosed异常!!!", e);
            }
        }
    }

}
