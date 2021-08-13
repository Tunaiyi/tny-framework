package com.tny.game.net.netty4.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NetPackDecoder {

	Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer, NetPackDecodeMarker marker) throws Exception;

}
