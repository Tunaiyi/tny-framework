package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@UnitInterface
public interface DatagramPackDecoder {

	Object decodeObject(ChannelHandlerContext ctx, final ByteBuf buffer, DatagramPackDecodeMarker marker) throws Exception;

}
