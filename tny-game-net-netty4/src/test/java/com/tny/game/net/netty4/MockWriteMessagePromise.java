package com.tny.game.net.netty4;

import com.tny.game.net.transport.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 */
public class MockWriteMessagePromise implements WriteMessagePromise {
    @Override
    public void success() {

    }

    @Override
    public void failed(Throwable cause) {

    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public long getWriteTimeout() {
        return 0;
    }

    @Override
    public void addWriteListener(WriteMessageListener listener) {

    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
