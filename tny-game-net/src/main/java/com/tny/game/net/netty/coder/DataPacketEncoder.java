package com.tny.game.net.netty.coder;

import io.netty.buffer.ByteBuf;


public interface DataPacketEncoder {

    void encodeObject(Object msg, ByteBuf out) throws Exception;

}
