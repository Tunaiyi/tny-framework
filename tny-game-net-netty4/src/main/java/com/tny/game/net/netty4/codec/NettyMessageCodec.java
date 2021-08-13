package com.tny.game.net.netty4.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;

@UnitInterface
public interface NettyMessageCodec {

	Message decode(ByteBuf bytes, MessageFactory factory) throws Exception;

	void encode(Message message, ByteBuf buffer) throws Exception;

}
