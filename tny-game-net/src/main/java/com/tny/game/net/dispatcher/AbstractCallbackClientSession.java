package com.tny.game.net.dispatcher;

import com.tny.game.net.LoginCertificate;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCallbackClientSession extends AbstractClientSession {

    protected AtomicBoolean futureMapLock = new AtomicBoolean(false);

    private volatile HashMap<Integer, MessageFuture<?>> futureMap;

    public AbstractCallbackClientSession(LoginCertificate loginInfo) {
        super(loginInfo);
    }

    @Override
    public MessageFuture<?> takeFuture(int id) {
        if (this.futureMap == null)
            return null;
        MessageFuture<?> future = null;
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                future = this.futureMap.remove(id);
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
        return future;
    }

    @Override
    public void putFuture(MessageFuture<?> future) {
        if (future == null)
            return;
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                if (this.futureMap == null)
                    this.futureMap = new HashMap<>();
                this.futureMap.put(future.getRequestID(), future);
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
    }

    @Override
    public void clearFuture() {
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                this.futureMap = new HashMap<>();
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
    }

}