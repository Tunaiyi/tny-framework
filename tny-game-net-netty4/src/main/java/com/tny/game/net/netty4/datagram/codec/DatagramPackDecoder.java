package com.tny.game.net.netty4.datagram.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface DatagramPackDecoder {

	Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer, DatagramPackDecodeMarker marker) throws Exception;

}
