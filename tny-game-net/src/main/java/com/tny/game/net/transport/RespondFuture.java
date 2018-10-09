package com.tny.game.net.transport;

import com.tny.game.net.transport.message.Message;

import java.util.concurrent.CompletableFuture;

public class RespondFuture<UID> extends NetStageableFuture<Message<UID>> {

    public static final long DEFAULT_FUTURE_LIFE_TIME = 30000L;

    private long timeout;

    public RespondFuture() {
        this(-1);
    }

    public RespondFuture(long timeout) {
        super(new CompletableFuture<>());
        if (timeout <= 0)
            timeout = DEFAULT_FUTURE_LIFE_TIME;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= timeout;
    }

}
