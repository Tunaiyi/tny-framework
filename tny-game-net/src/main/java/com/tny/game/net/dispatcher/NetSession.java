package com.tny.game.net.dispatcher;

public abstract class NetSession implements ServerSession, ClientSession {

    protected abstract MessageBuilderFactory getMessageBuilderFactory();

}