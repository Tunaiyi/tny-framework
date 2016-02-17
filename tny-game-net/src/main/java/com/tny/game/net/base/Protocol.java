package com.tny.game.net.base;

public interface Protocol {

    int getProtocol();

    default boolean isPush() {
        return false;
    }

    boolean isOwn(Message message);

}
