package com.tny.game.net.transport;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 */
public class MockWriteMessagePromise extends CompletableFuture<Void> implements WriteMessagePromise {

    private long timeout;

    private List<WriteMessageListener> listeners = new ArrayList<>();

    private RespondFuture respondFuture;

    public MockWriteMessagePromise(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setRespondFuture(RespondFuture respondFuture) {
        this.respondFuture = respondFuture;
    }

    @Override
    public void success() {
        this.complete(null);
    }

    @Override
    public <E extends Throwable> void failedAndThrow(E cause) throws E {
        this.completeExceptionally(cause);
        throw cause;
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
        if (!this.isDone()) {
            return null;
        }
        try {
            this.get();
        } catch (Throwable e) {
            return e;
        }
        return null;
    }

    @Override
    public long getWriteTimeout() {
        return this.timeout;
    }

    @Override
    public void addWriteListener(WriteMessageListener listener) {
        if (this.listeners.isEmpty()) {
            this.thenAccept((v) -> {
                for (WriteMessageListener ls : this.listeners)
                    ls.onWrite(this);
            });
        }
        this.listeners.add(listener);
    }

    @Override
    public void addWriteListeners(Collection<WriteMessageListener> listeners) {
        listeners.forEach(this::addWriteListener);
    }

}
