package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class VoidPacketArgumentsCodecor implements RelayPacketArgumentsCodecor<VoidPacketArguments> {

	@Override
	public Class<VoidPacketArguments> getArgumentsClass() {
		return VoidPacketArguments.class;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, VoidPacketArguments arguments, ByteBuf out) {
	}

	@Override
	public VoidPacketArguments decode(ChannelHandlerContext ctx, ByteBuf out) {
		return VoidPacketArguments.of();
	}

}
