package com.tny.game.net.dispatcher;

import java.util.Optional;

public abstract class NetServerSession implements ServerSession {

    protected abstract MessageBuilderFactory getMessageBuilderFactory();

    protected abstract int nextResponseNumber();

    protected abstract Optional<NetFuture> write(Object data);

}