package com.tny.game.relay.netty4;

import com.tny.game.net.netty4.*;
import com.tny.game.net.transport.*;
import com.tny.game.relay.*;
import com.tny.game.relay.packet.*;
import com.tny.game.relay.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:05 下午
 */
public class NettyRelayChannelTransmitter extends NettyChannelConnection implements RelayTransporter {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelMessageTransporter.class);

	public NettyRelayChannelTransmitter(Channel channel) {
		super(channel);
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return null;
	}

	@Override
	public void close() {
		NetRelayLink pipe = this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).getAndSet(null);
		if (pipe != null && pipe.isActive()) {
			pipe.close();
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
		this.channel.attr(NettyRelayAttrKeys.RELAY_LINK).set(link);
	}

}
