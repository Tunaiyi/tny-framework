package com.tny.game.net.dispatcher;

public abstract class CommonSession implements ServerSession, ClientSession {

    protected abstract MessageBuilderFactory getMessageBuilderFactory();

}