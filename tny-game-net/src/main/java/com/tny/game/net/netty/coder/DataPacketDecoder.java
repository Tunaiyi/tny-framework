package com.tny.game.net.netty.coder;

import io.netty.buffer.ByteBuf;

public interface DataPacketDecoder {

    Object decodeObject(final ByteBuf buffer) throws Exception;

}
