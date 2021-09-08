package com.tny.game.net.netty4.network;

import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
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

public class NettyClientGuide extends NettyBootstrap<NettyNetClientBootstrapSetting> implements ClientGuide {

	protected static final Logger LOGGER = LoggerFactory.getLogger(NetLogger.CLIENT);

	private static final boolean EPOLL = isEpoll();

	private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

	private Bootstrap bootstrap = null;

	private final Map<String, NettyClient<?>> clients = new ConcurrentHashMap<>();

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final ClientCloseListener<?> closeListener = (client) -> this.clients.remove(clientKey(client.getUrl()), as(client, NettyClient.class));

	public NettyClientGuide(NettyNetClientBootstrapSetting clientSetting) {
		super(clientSetting);
		buses().closeEvent().addListener(this.closeListener);
	}

	public NettyClientGuide(NettyNetClientBootstrapSetting clientSetting, ChannelMaker<Channel> channelMaker) {
		super(clientSetting, channelMaker);
		buses().closeEvent().addListener(this.closeListener);
	}

	private String clientKey(URL url) {
		return url.getHost() + ":" + url.getPort();
	}

	@Override
	public <UID> Client<UID> connect(URL url, PostConnect<UID> connect) {
		NetworkContext context = this.getContext();
		NettyClient<UID> client = new NettyClient<>(this, this.idGenerator, url, connect, context);
		NettyClient<UID> old = as(this.clients.putIfAbsent(clientKey(url), client));
		if (old != null) {
			return old;
		} else {
			client.open();
			return client;
		}
	}

	private Bootstrap getBootstrap() {
		if (this.bootstrap != null) {
			return this.bootstrap;
		}
		synchronized (this) {
			if (this.bootstrap != null) {
				return this.bootstrap;
			}
			this.bootstrap = new Bootstrap();
			NettyMessageHandler messageHandler = new NettyMessageHandler();
			this.bootstrap.group(workerGroup)
					.channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel channel) throws Exception {
							try {
								if (NettyClientGuide.this.channelMaker != null) {
									NettyClientGuide.this.channelMaker.initChannel(channel);
								}
								channel.pipeline().addLast("nettyMessageHandler", messageHandler);
							} catch (Throwable e) {
								LOGGER.info("init {} channel exception", channel, e);
								throw e;
							}
						}

					});
			return this.bootstrap;
		}

	}

	Channel connect(URL url, long connectTimeout) throws NetException {
		Asserts.checkNotNull(url, "url is null");
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
				throw new NetException(format("Connect url: {} failed. result: {} success: {}, connected: {}", url, result, success, connected),
						channelFuture.cause());
			} else {
				channelFuture.cancel(true);
				throw new NetException(format("Connect url: {} timeout. result: {}, success: {}, connected: {}", url, result, success, connected),
						channelFuture.cause());
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
		return this.closed.get();
	}

	@Override
	public boolean close() {
		if (this.closed.compareAndSet(false, true)) {
			this.clients.values().forEach(NettyClient::close);
			workerGroup.shutdownGracefully();
			buses().closeEvent().removeListener(this.closeListener);
			return true;
		}
		return false;
	}

}