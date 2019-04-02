package com.tny.game.net.message.coder;

import com.tny.game.common.unit.annotation.*;

@UnitInterface
public interface Codec<I> {

    I decode(byte[] bytes) throws Exception;

    byte[] encode(I object) throws Exception;

}