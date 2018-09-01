package com.tny.game.net.message;

import com.tny.game.net.session.CommonStageableFuture;

import java.util.concurrent.CompletableFuture;

public class RespondMessageFuture<UID> extends CommonStageableFuture<Message<UID>> {

    private long timeout;

    public RespondMessageFuture() {
        this(-1);
    }

    public RespondMessageFuture(long timeout) {
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
