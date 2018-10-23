package com.tny.game.net.message;

public interface Protocol {

    int PING_PONG_PROTOCOL_NUM = 0;

    /**
     * @return 协议号
     */
    int getProtocolNumber();

    default boolean isOwn(MessageHeader header) {
        return this.getProtocolNumber() == header.getProtocolNumber();
    }

    default boolean isOwn(Message message) {
        return this.getProtocolNumber() == message.getProtocol();
    }

}
