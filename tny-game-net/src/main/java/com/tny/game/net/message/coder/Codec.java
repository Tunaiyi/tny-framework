package com.tny.game.net.message.coder;

public interface Codec<I> {

    I decode(byte[] bodyBytes) throws Exception;

    byte[] encode(I message) throws Exception;

}