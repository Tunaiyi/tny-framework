package com.tny.game.net.netty4.relay.codec.arguments;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.netty4.datagram.codec.*;
import com.tny.game.net.netty4.relay.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.relay.packet.arguments.*;
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

	public TunnelRelayArgumentsCodecor(NettyMessageCodec messageCodec) {
		this.messageCodec = messageCodec;
	}

	@Override
	public Class<TunnelRelayArguments> getArgumentsClass() {
		return TunnelRelayArguments.class;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, TunnelRelayArguments arguments, ByteBuf out) throws Exception {
		NettyVarIntCoder.writeVarInt64(arguments.getTunnelId(), out);
		Message message = arguments.getMessage();
		messageCodec.encode(as(message), out);
	}

	@Override
	public TunnelRelayArguments decode(ChannelHandlerContext ctx, ByteBuf out) throws Exception {
		NetRelayTransporter transporter = ctx.channel().attr(NettyRelayAttrKeys.RELAY_TRANSPORTER).get();
		NetworkContext<?> context = transporter.getContext();
		long tunnelId = NettyVarIntCoder.readFixed64(out);
		NetMessage message = messageCodec.decode(out, context.getMessageFactory());
		return new TunnelRelayArguments(tunnelId, message);
	}

}
