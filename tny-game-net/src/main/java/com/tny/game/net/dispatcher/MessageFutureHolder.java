package com.tny.game.net.dispatcher;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Kun Yang on 16/9/20.
 */
public class MessageFutureHolder {

    protected AtomicBoolean futureMapLock = new AtomicBoolean(false);

    private volatile Map<Integer, MessageFuture<?>> futureMap = new HashMap<>();

    private long nextCheckTime;

    public MessageFutureHolder() {
        this.setNextCheckTime();
    }

    private void setNextCheckTime() {
        this.nextCheckTime = System.currentTimeMillis() + 3 * 60000;
    }

    private void checkTimeout() {
        if (System.currentTimeMillis() > this.nextCheckTime) {
            if (futureMap == null)
                return;
            List<Integer> ids = new ArrayList<>();
            for (Entry<Integer, MessageFuture<?>> entry : futureMap.entrySet()) {
                Integer id = entry.getKey();
                MessageFuture<?> future = futureMap.get(id);
                if (future.isTimeout())
                    ids.add(id);
            }
            if (!ids.isEmpty()) {
                ids.forEach(i -> {
                    MessageFuture<?> f = this.futureMap.remove(i);
                    if (f != null)
                        f.cancel(true);
                });
            }
            this.setNextCheckTime();
        }
    }

    public MessageFuture<?> takeFuture(int id) {
        if (this.futureMap == null)
            return null;
        MessageFuture<?> future = null;
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                if (this.futureMap == null)
                    return null;
                future = this.futureMap.remove(id);
                checkTimeout();
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
        return future;
    }

    public void putFuture(MessageFuture<?> future) {
        if (future == null)
            return;
        while (this.futureMapLock.compareAndSet(false, true)) {
            try {
                if (this.futureMap == null)
                    this.futureMap = new HashMap<>();
                this.checkTimeout();
                this.futureMap.put(future.getRequestID(), future);
                break;
            } finally {
                this.futureMapLock.set(false);
            }
        }
    }

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
