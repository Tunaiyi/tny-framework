package com.tny.game.relay.netty4.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NetRelayPackDecoder {

	Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer) throws Exception;

}
