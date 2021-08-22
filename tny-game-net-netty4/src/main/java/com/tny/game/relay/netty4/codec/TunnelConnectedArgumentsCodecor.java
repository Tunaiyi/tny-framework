package com.tny.game.relay.netty4.codec;

import com.tny.game.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class TunnelConnectedArgumentsCodecor implements RelayPacketArgumentsCodecor<TunnelConnectedArguments> {

	@Override
	public Class<TunnelConnectedArguments> getArgumentsClass() {
		return TunnelConnectedArguments.class;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, TunnelConnectedArguments arguments, ByteBuf out) {
		boolean result = arguments.getResult();
		out.writeByte(result ? 1 : 0);
	}

	@Override
	public TunnelConnectedArguments decode(ChannelHandlerContext ctx, ByteBuf out) {
		byte value = out.readByte();
		return value > 0 ? TunnelConnectedArguments.success() : TunnelConnectedArguments.failure();
	}

}
