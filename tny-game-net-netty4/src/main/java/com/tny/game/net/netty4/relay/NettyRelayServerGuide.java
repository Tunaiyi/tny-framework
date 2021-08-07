package com.tny.game.net.netty4.relay;

import com.google.common.collect.ImmutableSet;
import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.event.bus.*;
import com.tny.game.net.base.*;
import com.tny.game.net.base.listener.*;
import com.tny.game.net.netty4.*;
import com.tny.game.net.relay.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.*;

public class NettyRelayServerGuide extends NettyBootstrap<NettyServerBootstrapSetting> implements ServerGuide {

	protected static final Logger LOG = LoggerFactory.getLogger(NettyRelayServerGuide.class);

	private static final boolean EPOLL = isEpoll();

	private static final EventLoopGroup parentGroup = createLoopGroup(EPOLL, 1, "Sever-Boss-LoopGroup");

	private static final EventLoopGroup childGroup = createLoopGroup(EPOLL, Runtime.getRuntime().availableProcessors() * 2, "Sever-Child-LoopGroup");

	private volatile ServerBootstrap bootstrap;

	private final Collection<InetSocketAddress> bindAddresses;

	private final Map<String, Channel> channels = new CopyOnWriteMap<>();

	/**
	 * 服务器关闭监听器
	 */
	private final BindVoidEventBus<ServerClosedListener, ServerGuide> onClose = EventBuses.of(
			ServerClosedListener.class, ServerClosedListener::onClosed);

	public NettyRelayServerGuide(NettyServerBootstrapSetting setting) {
		super(setting);
		this.bindAddresses = ImmutableSet.copyOf(this.setting.getBindAddressList());
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
		LOG.info("#NettyTransitServer [ {} ] | 正在关闭服务器......", this.setting.getName());
		this.channels.forEach((address, channel) -> {
			try {
				NettyRelayServerGuide.LOG.info("#NettyTransitServer [ {} ] | Channel {} 关闭中......", this.setting.getName(), channel);
				channel.disconnect();
				NettyRelayServerGuide.LOG.info("#NettyTransitServer [ {} ] | Channel {} 关闭完成", this.setting.getName(), channel);
			} catch (Throwable e) {
				NettyRelayServerGuide.LOG.error("#NettyTransitServer [ {} ] | Channel {} 关闭异常!!!", this.setting.getName(), channel, e);
				LOG.error("NettyTransitServer [ {} ] | {} close exception", this.setting.getName(), address, e);
			}
		});
		parentGroup.shutdownGracefully();
		childGroup.shutdownGracefully();
		NettyRelayServerGuide.this.fireServerClosed();
		NettyRelayServerGuide.LOG.info("#NettyTransitServer [ {} ] | 服务器已关闭!!!", this.setting.getName());
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
		Channel channel = this.channels.get(addressString);
		if (channel != null) {
			if (channel.close().awaitUninterruptibly(30000L)) {
				this.channels.remove(addressString, channel);
			}
		}
		LOG.info("#NettyTransitServer [ {} ] | 正在打开监听{}端口", this.setting.getName(), address);
		ChannelFuture channelFuture = this.bootstrap().bind(address);
		if (channelFuture.awaitUninterruptibly(30000L)) {
			this.channels.put(addressString, channelFuture.channel());
			LOG.info("#NettyTransitServer [ {} ] | {}端口已监听", this.setting.getName(), address);
		} else {
			LOG.info("#NettyTransitServer [ {} ] | {}端口监听失败", this.setting.getName(), address);
		}
	}

	private ServerBootstrap bootstrap() {
		if (this.bootstrap != null) {
			return this.bootstrap;
		}
		synchronized (this) {
			if (this.bootstrap != null) {
				return this.bootstrap;
			}
			this.bootstrap = new ServerBootstrap();
			NettyRelayHandler datagramHandler = new NettyRelayHandler();
			this.bootstrap.group(parentGroup, childGroup);
			this.bootstrap.channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
			this.bootstrap.option(ChannelOption.SO_REUSEADDR, true);
			this.bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			this.bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			this.bootstrap.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel channel) throws Exception {
					ChannelMaker<Channel> maker = NettyRelayServerGuide.this.channelMaker;
					if (maker != null) {
						maker.initChannel(channel);
					}
					channel.pipeline().addLast("nettyTransitDatagramHandler", datagramHandler);
					openPipe(channel);
				}
			});
			return this.bootstrap;
		}

	}

	private void openPipe(Channel channel) {
		NetBootstrapContext<Object> context = NettyRelayServerGuide.this.getContext();
		RelayTransmitter transmitter = new NettyRelayChannelTransmitter(channel);
		NetPipe<?> pipe = new GeneralPipe<>(transmitter, context);
		pipe.open();
	}

}
