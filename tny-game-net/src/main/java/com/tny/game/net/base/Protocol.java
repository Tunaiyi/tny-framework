package com.tny.game.net.base;

public interface Protocol {

    int getProtocol();

    boolean isOwn(Message message);

}
