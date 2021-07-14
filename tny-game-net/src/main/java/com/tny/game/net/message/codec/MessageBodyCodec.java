package com.tny.game.net.message.codec;

import com.tny.game.common.unit.annotation.*;

@UnitInterface
public interface MessageBodyCodec<I> {

    I decode(byte[] bytes) throws Exception;

    byte[] encode(I object) throws Exception;

}