package com.tny.game.net.netty4.network.codec;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;
import io.netty.buffer.ByteBuf;

@UnitInterface
public interface NettyMessageCodec {

    NetMessage decode(ByteBuf bytes, MessageFactory factory) throws Exception;

    void encode(NetMessage message, ByteBuf buffer) throws Exception;

}
