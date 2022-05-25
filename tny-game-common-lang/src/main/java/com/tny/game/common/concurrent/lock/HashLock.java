package com.tny.game.common.concurrent.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class HashLock<L extends Lock> {

    private static final int DEFAULT_SIZE = 32;

    private int size;

    private Supplier<L> creator;

    private Map<Integer, L> lockMap = new ConcurrentHashMap<>();

    private static HashLock<Lock> lock = new HashLock<>(DEFAULT_SIZE);

    public static HashLock<Lock> common() {
        return lock;
    }

    public static HashLock<Lock> newInstance(int size) {
        return lock;
    }

    public static <L extends Lock> HashLock<L> newInstance(Supplier<L> creator) {
        return new HashLock<>(creator, DEFAULT_SIZE);
    }

    public static <L extends Lock> HashLock<L> newInstance(Supplier<L> creator, int size) {
        return new HashLock<>(creator, size);
    }

    private HashLock(Supplier<L> creator, int size) {
        this.creator = creator;
        this.size = size;
    }

    private HashLock(int size) {
        this.size = size;
        this.creator = () -> as(new ReentrantLock());
    }

    public L getLock(Object lockObject) {
        int hash = Math.abs(lockObject.hashCode() % this.size);
        return this.lockMap.computeIfAbsent(hash, (k) -> this.creator.get());
    }

}
