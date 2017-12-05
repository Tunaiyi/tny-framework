package com.tny.game.net.message.coder;

import com.tny.game.net.message.Message;

public interface MessageCoder {

    Message<?> decode(final byte[] array) throws Exception;

    byte[] encode(Message<?> message) throws Exception;

}
