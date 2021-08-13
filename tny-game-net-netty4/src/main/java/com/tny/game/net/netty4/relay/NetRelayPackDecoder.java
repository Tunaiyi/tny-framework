package com.tny.game.net.netty4.relay;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NetRelayPackDecoder {

	Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer) throws Exception;

}
