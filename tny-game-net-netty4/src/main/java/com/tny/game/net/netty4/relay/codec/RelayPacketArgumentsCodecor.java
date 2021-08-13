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
public interface RelayPacketArgumentsCodecor<A extends RelayPacketArguments> {

	Class<A> getArgumentsClass();

	void encode(ChannelHandlerContext ctx, A arguments, ByteBuf out);

	A decode(ChannelHandlerContext ctx, ByteBuf out);

}
