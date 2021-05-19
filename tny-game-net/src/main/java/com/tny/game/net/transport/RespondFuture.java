package com.tny.game.net.transport;

import com.tny.game.net.message.*;

import java.util.concurrent.CompletableFuture;

public class RespondFuture extends CompletableFuture<Message> {

    public static final long DEFAULT_FUTURE_TIMEOUT = 10000L;

    private long timeout;

    public RespondFuture() {
        this(-1);
    }

    public RespondFuture(long timeout) {
        if (timeout <= 0) {
            timeout = DEFAULT_FUTURE_TIMEOUT;
        }
        this.timeout = System.currentTimeMillis() + timeout;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= this.timeout;
    }

}