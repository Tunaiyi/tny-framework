package com.tny.game.net.netty4.relay;

import com.tny.game.net.base.*;
import com.tny.game.net.netty4.channel.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import java.util.function.Consumer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:05 下午
 */
public class NettyChannelRelayTransporter extends NettyChannelConnection implements NetRelayTransporter {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelMessageTransporter.class);

	private final NetworkContext context;

	public NettyChannelRelayTransporter(Channel channel, NetworkContext context) {
		super(channel);
		this.context = context;
		this.channel.attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).setIfAbsent(this);
		this.channel.closeFuture().addListener(f -> this.close());
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return new NettyWriteMessagePromise();
	}

	@Override
	public void close() {
		NetRelayLink link = this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).getAndSet(null);
		if (link != null) {
			link.disconnect();
		}
		this.channel.disconnect();
	}

	@Override
	public WriteMessageFuture write(RelayPacket<?> packet, WriteMessagePromise promise) {
		ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
		this.channel.writeAndFlush(packet, channelPromise);
		return promise;
	}

	@Override
	public WriteMessageFuture write(RelayPacketMaker maker, WriteMessagePromise promise) {
		ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
		this.channel.eventLoop().execute(() -> this.channel.writeAndFlush(maker.make(), channelPromise));
		return promise;
	}

	@Override
	public void bind(NetRelayLink link) {
		this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).setIfAbsent(link);
	}

	@Override
	public NetworkContext getContext() {
		return context;
	}

	@Override
	public void addCloseListener(Consumer<NetRelayTransporter> onClose) {
		this.channel.closeFuture().addListener((f) -> onClose.accept(this));
	}

}
