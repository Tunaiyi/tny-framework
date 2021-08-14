package com.tny.game.net.netty4.relay.codec;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.packet.arguments.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:01 下午
 */
public class TubuleMessageArgumentsCodecor implements RelayPacketArgumentsCodecor<TubuleMessageArguments> {

	@Override
	public Class<TubuleMessageArguments> getArgumentsClass() {
		return TubuleMessageArguments.class;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, TubuleMessageArguments arguments, ByteBuf out) {
		Message message = arguments.getMessage();
	}

	@Override
	public TubuleMessageArguments decode(ChannelHandlerContext ctx, ByteBuf out) {
		return null;
	}

}
