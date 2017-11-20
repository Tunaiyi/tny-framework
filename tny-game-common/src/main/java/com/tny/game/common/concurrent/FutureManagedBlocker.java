package com.tny.game.common.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool.ManagedBlocker;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kun Yang on 2017/11/17.
 */
public class FutureManagedBlocker implements ManagedBlocker {

    public static final Logger LOGGER = LoggerFactory.getLogger(FutureManagedBlocker.class);

    private Future<?> future;

    private long timeout;

    public FutureManagedBlocker(Future<?> future, long timeout) {
        this.future = future;
        this.timeout = timeout;
    }

    @Override
    public boolean block() throws InterruptedException {
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | TimeoutException e) {
            LOGGER.error("", e);
        }
        return isReleasable();
    }

    @Override
    public boolean isReleasable() {
        return future.isDone();
    }
}
