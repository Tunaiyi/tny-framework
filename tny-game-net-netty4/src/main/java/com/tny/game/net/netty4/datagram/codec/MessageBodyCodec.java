package com.tny.game.net.netty4.datagram.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import io.netty.buffer.ByteBuf;

@UnitInterface
public interface MessageBodyCodec<I> {

	I decode(ByteBuf buffer) throws Exception;

	void encode(I object, ByteBuf buffer) throws Exception;

}