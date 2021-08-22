package com.tny.game.relay.netty4.codec;

import com.tny.game.relay.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NetRelayPackEncoder {

	void encodeObject(ChannelHandlerContext ctx, RelayPacket<?> relay, ByteBuf out) throws Exception;

}
