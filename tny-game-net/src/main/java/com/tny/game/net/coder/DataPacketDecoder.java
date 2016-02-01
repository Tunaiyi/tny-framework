package com.tny.game.net.coder;

import io.netty.buffer.ByteBuf;

public interface DataPacketDecoder {

    public Object decodeObject(final ByteBuf buffer) throws Exception;

}
