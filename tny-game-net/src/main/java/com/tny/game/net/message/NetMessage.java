package com.tny.game.net.message;

public interface NetMessage extends NetMessageHead, Message {

    void relay(boolean value);

}
