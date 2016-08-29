package com.tny.game.net.coder;

public interface MessageBodyCoder {

    Object doDecoder(final byte[] array, boolean isRequest) throws Exception;

    byte[] doEncode(Object message) throws Exception;

}
