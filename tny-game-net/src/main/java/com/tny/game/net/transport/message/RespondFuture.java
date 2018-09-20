package com.tny.game.net.transport.message;

import com.tny.game.net.transport.CommonStageableFuture;

import java.util.concurrent.CompletableFuture;

public class RespondFuture<UID> extends CommonStageableFuture<Message<UID>> {

    private long timeout;

    public RespondFuture() {
        this(-1);
    }

    public RespondFuture(long timeout) {
        super(new CompletableFuture<>());
        if (timeout <= 0)
            timeout = 30000L;
        this.timeout = System.currentTimeMillis() + timeout;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() >= timeout;
    }

    public void complete(Message<UID> message) {
        this.future().complete(message);
    }

}
