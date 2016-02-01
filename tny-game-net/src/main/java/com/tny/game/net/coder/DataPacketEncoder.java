package com.tny.game.net.coder;

import io.netty.buffer.ByteBuf;


public interface DataPacketEncoder {

    public void encodeObject(Object msg, ByteBuf out) throws Exception;

}
