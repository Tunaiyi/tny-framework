package com.tny.game.net.dispatcher;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ResponseMonitorHolder {

    private Map<Class<?>, List<ResponseMonitor<?>>> monitorMap = new HashMap<Class<?>, List<ResponseMonitor<?>>>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private Lock readLock = this.lock.readLock();
    private Lock writeLock = this.lock.writeLock();

    public ResponseMonitorHolder() {
    }

    ;

    public synchronized void register(Collection<ResponseMonitor<?>> collection) {
        this.writeLock.lock();
        try {
            for (ResponseMonitor<?> monitor : collection)
                this.register(monitor);
        } finally {
            this.writeLock.unlock();
        }
    }

    public synchronized void register(ResponseMonitor<?> responseMonitor) {
        this.writeLock.lock();
        try {
            List<ResponseMonitor<?>> list = this.monitorMap.get(responseMonitor.getMessageClass());
            if (list == null) {
                list = new CopyOnWriteArrayList<ResponseMonitor<?>>();
                this.monitorMap.put(responseMonitor.getMessageClass(), list);
            }
            list.add(responseMonitor);
        } finally {
            this.writeLock.unlock();
        }
    }

    public synchronized void unregister(ResponseMonitor<?> responseMonitor) {
        this.writeLock.lock();
        try {
            List<ResponseMonitor<?>> list = this.monitorMap.get(responseMonitor.getMessageClass());
            if (list != null) {
                list.remove(responseMonitor);
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    public List<ResponseMonitor<?>> getMonitorHolderList(Object message) {
        this.readLock.lock();
        try {
            List<ResponseMonitor<?>> list = this.monitorMap.get(message.getClass());
            if (list != null)
                return Collections.unmodifiableList(list);
            return Collections.emptyList();
        } finally {
            this.readLock.unlock();
        }
    }

}
