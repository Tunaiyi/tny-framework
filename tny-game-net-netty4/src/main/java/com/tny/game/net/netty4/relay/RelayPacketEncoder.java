package com.tny.game.net.netty4.relay;

import com.tny.game.net.relay.packet.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface RelayPacketEncoder {

	void encodeObject(ChannelHandlerContext ctx, RelayPacket relay, ByteBuf out) throws Exception;

}
