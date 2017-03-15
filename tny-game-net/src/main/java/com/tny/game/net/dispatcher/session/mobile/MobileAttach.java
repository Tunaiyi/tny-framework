package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Response;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MobileAttach {

    private AtomicInteger sessionState = new AtomicInteger(MobileSessionState.ONLINE.getId());

    private int requestIDCounter = -1;
    private AtomicInteger number = new AtomicInteger(0);
    private CircularFifoQueue<ResponseItem> responseCache;
    private volatile long offlineTimeout = -1;

    private StampedLock lock = new StampedLock();

    public MobileAttach(int size, MobileAttach attach) {
        this(size);
        this.responseCache.addAll(attach.responseCache);
    }

    public MobileAttach(int size) {
        this.responseCache = new CircularFifoQueue<>(size);
    }

    public boolean isState(MobileSessionState state) {
        return this.sessionState.get() == state.getId();
    }

    public List<ResponseItem> getAll() {
        long stamp = lock.readLock();
        try {
            return new ArrayList<>(this.responseCache);
        } finally {
            lock.unlock(stamp);
        }
    }

    public ResponseItem get(int id) {
        long stamp = lock.readLock();
        try {
            return this.responseCache.stream()
                    .filter(r -> r.getID() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock(stamp);
        }
    }

    public void asStream(Consumer<Stream<ResponseItem>> consumer) {
        long stamp = lock.readLock();
        try {
            consumer.accept(this.responseCache.stream());
        } finally {
            lock.unlock(stamp);
        }
    }

    protected void push(Response response) {
        long stamp = lock.readLock();
        try {
            while (true) {
                if (!responseCache.isEmpty()) {
                    ResponseItem item = responseCache.get(responseCache.size() - 1);
                    if (item != null && item.getNumber() >= response.getNumber())
                        return;
                }
                long writeStamp = lock.tryConvertToWriteLock(stamp);
                if (writeStamp != 0L) {
                    stamp = writeStamp;
                    this.responseCache.add(new ResponseItem(response.getID(), response));
                    return;
                } else {
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }

    public boolean isOfflineTimeout(long time) {
        return this.offlineTimeout > 0 && time >= this.offlineTimeout;
    }

    protected boolean online() {
        if (this.sessionState.compareAndSet(MobileSessionState.OFFLINE.getId(), MobileSessionState.ONLINE.getId())) {
            this.offlineTimeout = -1;
            return true;
        }
        return false;
    }

    protected boolean offline(long offlineWaitMills) {
        if (this.sessionState.compareAndSet(MobileSessionState.ONLINE.getId(), MobileSessionState.OFFLINE.getId())) {
            this.offlineTimeout = System.currentTimeMillis() + offlineWaitMills;
            return true;
        }
        return false;
    }

    protected boolean invalid() {
        return this.sessionState.compareAndSet(MobileSessionState.OFFLINE.getId(), MobileSessionState.INVALID.getId());
    }

    int createResponseNumber() {
        return number.incrementAndGet();
    }

    protected boolean checkAndUpdate(Request request) {
        if (request.getID() > this.requestIDCounter) {
            this.requestIDCounter = request.getID();
            return true;
        }
        return false;
    }


}
