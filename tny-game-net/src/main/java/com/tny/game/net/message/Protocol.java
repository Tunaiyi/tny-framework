package com.tny.game.net.message;

public interface Protocol {

    int getProtocol();

    default boolean isPush() {
        return false;
    }

    default boolean isOwn(Message message) {
        return this.getProtocol() == message.getProtocol();
    }

}
