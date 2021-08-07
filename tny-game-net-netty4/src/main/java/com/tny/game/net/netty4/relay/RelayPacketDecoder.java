package com.tny.game.net.netty4.relay;

import com.tny.game.net.netty4.codec.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface RelayPacketDecoder {

	Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer, DataPacketMarker marker) throws Exception;

}
