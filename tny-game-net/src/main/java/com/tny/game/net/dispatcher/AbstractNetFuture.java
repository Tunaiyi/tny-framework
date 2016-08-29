package com.tny.game.net.dispatcher;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by Kun Yang on 16/8/9.
 */
public abstract class AbstractNetFuture<F extends Future<Void>> implements NetFuture {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractNetFuture.class);

    protected Session session;

    protected F future;

    private List<Consumer<NetFuture>> listeners;

    protected AbstractNetFuture(Session session, F future) {
        this.session = session;
        this.future = future;
    }

    protected List<Consumer<NetFuture>> getListeners() {
        return ObjectUtils.defaultIfNull(this.listeners, Collections.emptyList());
    }

    protected List<Consumer<NetFuture>> getAndCreateListeners() {
        if (listeners != null)
            return listeners;
        listeners = new ArrayList<>();
        this.addRealListener(() -> listeners.forEach(this::doFire));
        return this.listeners;
    }

    protected abstract void addRealListener(Runnable firer);

    private void doFire(Consumer<NetFuture> listener) {
        try {
            listener.accept(this);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }


    @Override
    public Session getSession() {
        return session;
    }


    @Override
    public NetFuture addListener(Consumer<NetFuture> listener) {
        getAndCreateListeners().add(listener);
        return this;
    }

    @Override
    public NetFuture removeListener(Consumer<NetFuture> listener) {
        List<Consumer<NetFuture>> listeners = this.listeners;
        if (listeners != null)
            listeners.remove(listener);
        return this;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

}
