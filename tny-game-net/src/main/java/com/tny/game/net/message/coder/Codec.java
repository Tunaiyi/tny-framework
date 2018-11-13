package com.tny.game.net.message.coder;

import com.tny.game.common.unit.annotation.UnitInterface;

@UnitInterface
public interface Codec<I> {

    I decode(byte[] bodyBytes) throws Exception;

    byte[] encode(I message) throws Exception;

}