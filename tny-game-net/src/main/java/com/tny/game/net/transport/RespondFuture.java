package com.tny.game.net.transport;

import com.tny.game.net.message.Message;

import java.util.concurrent.CompletableFuture;

public class RespondFuture<UID> extends CompletableFuture<Message<UID>> {

    public static final long DEFAULT_FUTURE_TIMEOUT = 30000L;

    private long timeout;

    public RespondFuture() {
        this(-1);
    }

    public RespondFuture(long timeout) {
        if (timeout <= 0)
            timeout = DEFAULT_FUTURE_TIMEOUT;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= timeout;
    }

}
