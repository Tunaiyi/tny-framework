package com.tny.game.net.message;

public interface Protocol {

    int PING_PROTOCOL_NUM = 1;

    int PONG_PROTOCOL_NUM = 2;

    int getProtocol();

    default boolean isOwn(Message message) {
        return this.getProtocol() == message.getProtocol();
    }

}
