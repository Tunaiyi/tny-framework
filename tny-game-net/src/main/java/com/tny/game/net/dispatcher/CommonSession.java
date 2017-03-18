package com.tny.game.net.dispatcher;

import com.tny.game.net.message.MessageBuilderFactory;

public abstract class CommonSession implements ServerSession, ClientSession {

    protected abstract MessageBuilderFactory getMessageBuilderFactory();

}