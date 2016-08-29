package com.tny.game.net.dispatcher;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ResponseHandlerHolder {

    private Map<Class<?>, List<ResponseHandler<?>>> monitorMap = new HashMap<Class<?>, List<ResponseHandler<?>>>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private Lock readLock = this.lock.readLock();
    private Lock writeLock = this.lock.writeLock();

    public ResponseHandlerHolder() {
    }

    ;

    public synchronized void register(Collection<ResponseHandler<?>> collection) {
        this.writeLock.lock();
        try {
            for (ResponseHandler<?> monitor : collection)
                this.register(monitor);
        } finally {
            this.writeLock.unlock();
        }
    }

    public synchronized void register(ResponseHandler<?> responseHandler) {
        this.writeLock.lock();
        try {
            List<ResponseHandler<?>> list = this.monitorMap.get(responseHandler.getMessageClass());
            if (list == null) {
                list = new CopyOnWriteArrayList<ResponseHandler<?>>();
                this.monitorMap.put(responseHandler.getMessageClass(), list);
            }
            list.add(responseHandler);
        } finally {
            this.writeLock.unlock();
        }
    }

    public synchronized void unregister(ResponseHandler<?> responseHandler) {
        this.writeLock.lock();
        try {
            List<ResponseHandler<?>> list = this.monitorMap.get(responseHandler.getMessageClass());
            if (list != null) {
                list.remove(responseHandler);
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    public List<ResponseHandler<?>> getMonitorHolderList(Object message) {
        this.readLock.lock();
        try {
            List<ResponseHandler<?>> list = this.monitorMap.get(message.getClass());
            if (list != null)
                return Collections.unmodifiableList(list);
            return Collections.emptyList();
        } finally {
            this.readLock.unlock();
        }
    }

}
