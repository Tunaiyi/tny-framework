package com.tny.game.net.transport.message;

public interface Protocol {

    int PING_PONG_PROTOCOL_NUM = 0;

    int getProtocol();

    default boolean isOwn(MessageHeader header) {
        return this.getProtocol() == header.getProtocol();
    }

    default boolean isOwn(Message message) {
        return this.getProtocol() == message.getHeader().getProtocol();
    }

}
