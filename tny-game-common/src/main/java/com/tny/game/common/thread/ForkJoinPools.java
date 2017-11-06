package com.tny.game.common.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by Kun Yang on 2017/11/3.
 */
public class ForkJoinPools {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinPools.class);

    private static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final Map<String, ForkJoinPool> poolMap = new ConcurrentHashMap<>();


    public static ForkJoinPool pool(int threads, String name, boolean asyncMode) {
        return poolMap.computeIfAbsent(name, (k) -> new ForkJoinPool(threads, new CoreThreadFactory(name),
                (t, e) -> LOGGER.error("{} 运行异常", name, e), asyncMode));
    }

    public static ForkJoinPool pool(int threads, String name) {
        return pool(threads, name, false);
    }

    public static ForkJoinPool pool(String name) {
        return pool(DEFAULT_SIZE, name, false);
    }

    public static ForkJoinPool commonPool() {
        return ForkJoinPool.commonPool();
    }

}
