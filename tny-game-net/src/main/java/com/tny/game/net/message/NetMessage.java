package com.tny.game.net.message;

public interface NetMessage extends NetMessageHead, Message {

    boolean isRelay();

    void relay(boolean value);

}
