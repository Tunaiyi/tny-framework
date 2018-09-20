package com.tny.game.net.transport.message.coder;

import com.tny.game.net.transport.message.Message;

public interface MessageCoder {

    Message<?> decode(final byte[] array) throws Exception;

    byte[] encode(Message<?> message) throws Exception;

}
