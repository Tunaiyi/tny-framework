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
public class NettyChannelRelayTransporter extends NettyChannelConnection implements RelayTransporter {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelMessageTransporter.class);

	private final NetworkContext context;

	public NettyChannelRelayTransporter(Channel channel, NetworkContext context) {
		super(channel);
		this.context = context;
		this.channel.attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).setIfAbsent(this);
		this.channel.closeFuture().addListener(f -> this.close());
	}

	@Override
	protected void doClose() {
		NetRelayLink link = this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).getAndSet(null);
		if (link != null) {
			link.disconnect();
		}
		this.channel.disconnect();
	}

	@Override
	public MessageWriteAwaiter write(RelayPacket<?> packet, MessageWriteAwaiter awaiter) {
		ChannelPromise channelPromise = createChannelPromise(awaiter);
		this.channel.writeAndFlush(packet, channelPromise);
		return awaiter;
	}

	@Override
	public MessageWriteAwaiter write(RelayPacketMaker maker, MessageWriteAwaiter awaiter) {
		ChannelPromise channelPromise = createChannelPromise(awaiter);
		this.channel.eventLoop().execute(() -> this.channel.writeAndFlush(maker.make(), channelPromise));
		return awaiter;
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
	public void addCloseListener(Consumer<RelayTransporter> onClose) {
		this.channel.closeFuture().addListener((f) -> onClose.accept(this));
	}

}
