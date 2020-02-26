package com.tny.game.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-20 21:58
 */
public class PID<M> {

    private static final Map<Integer, PID<?>> PID_MAP = new ConcurrentHashMap<>();
    private int pid;

    public int getPid() {
        return this.pid;
    }

    private PID(int pid) {
        this.pid = pid;
    }

    public static <T> PID<T> create(int pid) {
        PID<T> pidObject = new PID<>(pid);
        PID<?> old = PID_MAP.putIfAbsent(pidObject.pid, pidObject);
        if (old != null)
            throw new IllegalArgumentException();
        return pidObject;
    }

}
