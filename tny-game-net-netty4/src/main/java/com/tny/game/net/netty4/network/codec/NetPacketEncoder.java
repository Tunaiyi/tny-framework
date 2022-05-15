package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@UnitInterface
public interface NetPacketEncoder {

    void encodeObject(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception;

}
