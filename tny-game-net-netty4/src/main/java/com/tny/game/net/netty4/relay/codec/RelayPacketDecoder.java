package com.tny.game.net.netty4.relay.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface RelayPacketDecoder {

    Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer) throws Exception;

}
