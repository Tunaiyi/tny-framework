package com.tny.game.net.netty4.codec;

import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface NetPackEncoder {

	void encodeObject(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception;

}
