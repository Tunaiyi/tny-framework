package com.tny.game.net.netty4.relay;

import com.tny.game.net.netty4.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.net.netty4.NettyAttrKeys.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:05 下午
 */
public class NettyRelayChannelTransmitter extends NettyChannelConnection implements RelayTransmitter {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelTransporter.class);

	public NettyRelayChannelTransmitter(Channel channel) {
		super(channel);
	}

	@Override
	public WriteMessagePromise createWritePromise(long sendTimeout) {
		return null;
	}

	@Override
	public void close() {
		NetRelayPipe<?> pipe = this.channel.attr(PIPE).getAndSet(null);
		if (pipe != null && pipe.isActive()) {
			pipe.close();
		}
		this.channel.disconnect();
	}

	@Override
	public WriteMessageFuture write(RelayPacket packet, WriteMessagePromise promise) {
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
	public void bind(GeneralRelayPipe<?> pipe) {
		this.channel.attr(PIPE).set(pipe);
	}

}
