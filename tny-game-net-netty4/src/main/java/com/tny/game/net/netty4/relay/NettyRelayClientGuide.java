package com.tny.game.net.netty4.relay;

import com.tny.game.common.url.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.listener.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.datagram.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
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

public class NettyRelayClientGuide extends NettyBootstrap<NettyRelayClientBootstrapSetting> implements RelayClientGuide {

	protected static final Logger LOG = LoggerFactory.getLogger(NetLogger.CLIENT);

	private static final boolean EPOLL = isEpoll();

	private static final EventLoopGroup workerGroup = createLoopGroup(EPOLL, 1, "Client-Work-LoopGroup");

	private Bootstrap bootstrap = null;

	private final Map<String, NettyClient<?>> clients = new ConcurrentHashMap<>();

	private final AtomicBoolean closed = new AtomicBoolean(false);

	private final ClientCloseListener<?> closeListener = (client) -> this.clients.remove(clientKey(client.getUrl()), as(client, NettyClient.class));

	public NettyRelayClientGuide(NettyRelayClientBootstrapSetting clientSetting) {
		super(clientSetting);
		buses().closeEvent().addListener(this.closeListener);
	}

	public NettyRelayClientGuide(NettyRelayClientBootstrapSetting clientSetting, ChannelMaker<Channel> channelMaker) {
		super(clientSetting, channelMaker);
		buses().closeEvent().addListener(this.closeListener);
	}

	private String clientKey(URL url) {
		return url.getHost() + ":" + url.getPort();
	}

	@Override
	public void connect(URL url, long timeout, RelayConnectCallback callback) {
		Asserts.checkNotNull(url, "url is null");
		ChannelFuture channelFuture = this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
		channelFuture.addListener(future -> {
			if (future.isSuccess()) {
				callback.complete(true, url, createNetRelayLink(channelFuture.channel()), null);
			} else {
				callback.complete(false, url, null, future.cause());
			}
		});
	}

	@Override
	public NetRelayTransporter connect(URL url, long connectTimeout) throws NetException {
		Asserts.checkNotNull(url, "url is null");
		ChannelFuture channelFuture = null;
		try {
			channelFuture = this.getBootstrap().connect(new InetSocketAddress(url.getHost(), url.getPort()));
			boolean result = channelFuture.awaitUninterruptibly(connectTimeout, TimeUnit.MILLISECONDS);
			boolean success = channelFuture.isSuccess();
			if (result && success) {
				return createNetRelayLink(channelFuture.channel());
			}
			if (channelFuture.cause() != null) {
				throw new NetException(format("Connect url: {} failed. result: {} success: {}", url, result, success),
						channelFuture.cause());
			} else {
				throw new NetException(format("Connect url: {} timeout. result: {}, success: {}", url, result, success),
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
	protected void onLoadUnit(NettyRelayClientBootstrapSetting setting) {
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
			RelayPacketProcessor relayPacketProcessor = new LocalRelayPacketProcessor();
			NettyRelayPacketHandler relayMessageHandler = new NettyRelayPacketHandler(relayPacketProcessor);
			this.bootstrap.group(workerGroup)
					.channel(EPOLL ? EpollSocketChannel.class : NioSocketChannel.class)
					.option(ChannelOption.SO_REUSEADDR, true)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<Channel>() {

						@Override
						protected void initChannel(Channel ch) throws Exception {
							if (NettyRelayClientGuide.this.channelMaker != null) {
								NettyRelayClientGuide.this.channelMaker.initChannel(ch);
							}
							ch.pipeline().addLast("nettyMessageHandler", relayMessageHandler);
						}

					});
			return this.bootstrap;
		}

	}

	private NetRelayTransporter createNetRelayLink(Channel channel) {
		return new NettyChannelRelayTransporter(channel, this.getContext());
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