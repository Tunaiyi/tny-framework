package com.tny.game.net.message.codec;

import com.tny.game.common.unit.annotation.*;

import java.nio.ByteBuffer;

@UnitInterface
public interface MessageBodyCodec<I> {

    I decode(ByteBuffer buffer) throws Exception;

    ByteBuffer encode(I object) throws Exception;

}