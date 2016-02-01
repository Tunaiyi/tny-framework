package com.tny.game.net.coder;

public interface MessageBodyCoder {

    public Object doDecoder(final byte[] array, boolean isRequset) throws Exception;

    public byte[] doEncode(Object message) throws Exception;

}
