package com.tny.game.net.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 */
public class MockWriteMessagePromise extends CompletableFuture<Void> implements WriteMessagePromise {

    private long timeout;

    private List<WriteMessageListener> listeners = new ArrayList<>();

    private RespondFuture<?> respondFuture;

    public MockWriteMessagePromise(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setRespondFuture(RespondFuture<?> respondFuture) {
        this.respondFuture = respondFuture;
    }

    @Override
    public void success() {
        this.complete(null);
    }

    @Override
    public void failed(Throwable cause) {
        this.completeExceptionally(cause);
    }

    @Override
    public boolean isSuccess() {
        return this.isDone() && this.cause() != null;
    }

    @Override
    public Throwable cause() {
        if (!this.isDone())
            return null;
        try {
            this.get();
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    @Override
    public long getWriteTimeout() {
        return timeout;
    }

    @Override
    public void addWriteListener(WriteMessageListener listener) {
        if (this.listeners.isEmpty())
            this.thenAccept((v) -> {
                for (WriteMessageListener ls : listeners)
                    ls.onWrite(this);
            });
        this.listeners.add(listener);
    }
}
