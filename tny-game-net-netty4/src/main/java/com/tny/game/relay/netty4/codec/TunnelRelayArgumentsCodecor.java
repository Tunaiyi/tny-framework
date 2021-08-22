package com.tny.game.relay.netty4.codec;

import com.tny.game.net.message.*;
import com.tny.game.net.netty4.codec.*;
import com.tny.game.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class TunnelRelayArgumentsCodecor implements RelayPacketArgumentsCodecor<TunnelRelayArguments> {

	private final NettyMessageCodec messageCodec;

	private final MessageFactory factory;

	public TunnelRelayArgumentsCodecor(NettyMessageCodec messageCodec, MessageFactory factory) {
		this.messageCodec = messageCodec;
		this.factory = factory;
	}

	@Override
	public Class<TunnelRelayArguments> getArgumentsClass() {
		return TunnelRelayArguments.class;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, TunnelRelayArguments arguments, ByteBuf out) throws Exception {
		Message message = arguments.getMessage();
		messageCodec.encode(as(message), out);
	}

	@Override
	public TunnelRelayArguments decode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
		NetMessage message = messageCodec.decode(out, factory);
		return new TunnelRelayArguments(message);
	}

}
