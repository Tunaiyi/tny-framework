package com.tny.game.net.base;

public abstract class NetMessage implements Message {

    @Override
    public boolean isOwn(Message message) {
        return this.getProtocol() == message.getProtocol();
    }



}
