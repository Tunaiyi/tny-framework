package com.tny.game.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-12 21:09
 */
public class HashLock<L extends Lock> {

    private int size;

    private Supplier<L> creator;

    private Map<Integer, L> lockMap = new ConcurrentHashMap<>();

    private static HashLock<Lock> lock = new HashLock<>(5000);

    public static HashLock<Lock> common() {
        return lock;
    }

    public HashLock(int size, Supplier<L> creator) {
        this.size = size;
        this.creator = creator;
    }

    public HashLock(int size) {
        this.size = size;
        this.creator = () -> as(new ReentrantLock());
    }

    public L getLock(Object lockObject) {
        int hash = Math.abs(lockObject.hashCode() % size);
        return lockMap.computeIfAbsent(hash, (k) -> creator.get());
    }

}
