package com.tny.game.net.transport.message;

public interface Protocol {

    int PING_PONG_PROTOCOL_NUM = 0;

    int getNumber();

    default boolean isOwn(MessageHeader header) {
        return this.getNumber() == header.getNumber();
    }

    default boolean isOwn(Message message) {
        return this.getNumber() == message.getProtocol();
    }

}
