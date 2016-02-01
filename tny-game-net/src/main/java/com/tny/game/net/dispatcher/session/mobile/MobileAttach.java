package com.tny.game.net.dispatcher.session.mobile;

import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.Session;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MobileAttach {

    private AtomicReference<MobileSessionState> sessionState = new AtomicReference<>(MobileSessionState.ONLINE);

    private int requestIDCounter = -1;

    private Map<Integer, ResponseItem> sentResponseCache;//= new HashMap<Integer, Response>();

    private long offlineWait;

    private long offlineTimeout = -1;

    private long leaveWait;

    private long leaveTimeout = -1;

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = this.lock.readLock();
    private Lock writeLock = this.lock.writeLock();

    public MobileAttach(int size, long leaveWait, long offlineWait, MobileAttach attach) {
        this(size, leaveWait, offlineWait);
        this.sentResponseCache.putAll(attach.sentResponseCache);
    }

    public MobileAttach(int size, long leaveWait, long offlineWait) {
        this.offlineWait = offlineWait;
        this.leaveWait = Math.min(this.offlineWait, this.leaveWait);
        this.sentResponseCache = new FixLinkedHashMap<>(size);
    }

    public boolean exsist(int id) {
        this.readLock.lock();
        try {
            return this.sentResponseCache.containsKey(id);
        } finally {
            this.readLock.unlock();
        }
    }

    public ResponseItem get(int id) {
        this.readLock.lock();
        try {
            return this.sentResponseCache.get(id);
        } finally {
            this.readLock.unlock();
        }
    }

    protected void push(ResponseItem response) {
        if (response.getID() <= Session.PUSH_RESPONSE_ID)
            return;
        this.writeLock.lock();
        try {
            this.sentResponseCache.put(response.getID(), response);
        } finally {
            this.writeLock.unlock();
        }
    }

    public MobileSessionState getSessionState() {
        return this.sessionState.get();
    }

    public boolean isOfflineTimeout() {
        return this.offlineTimeout > 0 && System.currentTimeMillis() >= this.offlineTimeout;
    }

    public boolean isLeaveTimeout() {
        return this.leaveTimeout > 0 && System.currentTimeMillis() >= this.leaveTimeout;
    }

    public boolean isLeaveTimeout(long now) {
        return this.leaveTimeout > 0 && now >= this.leaveTimeout;
    }

    protected boolean online() {
        if (this.sessionState.compareAndSet(MobileSessionState.OFFLINE, MobileSessionState.ONLINE)) {
            this.offlineWait = -1;
            return true;
        }
        return false;
    }

    protected boolean offline() {
        if (this.sessionState.compareAndSet(MobileSessionState.ONLINE, MobileSessionState.OFFLINE)) {
            long now = System.currentTimeMillis();
            this.offlineTimeout = now + this.offlineWait;
            this.leaveTimeout = now + this.leaveWait;
            return true;
        }
        return false;
    }

    protected boolean invalid() {
        return this.sessionState.compareAndSet(MobileSessionState.OFFLINE, MobileSessionState.INVALID);
    }

    public int getRequestIDCounter() {
        return this.requestIDCounter;
    }

    public boolean checkAndUpdate(Request request) {
        if (request.getID() > this.requestIDCounter) {
            this.requestIDCounter = request.getID();
            return true;
        }
        return false;
    }

}
