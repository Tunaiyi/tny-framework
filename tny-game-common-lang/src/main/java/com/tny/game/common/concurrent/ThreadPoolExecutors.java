/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.concurrent;

import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/**
 * Created by Kun Yang on 2017/11/3.
 */
public class ThreadPoolExecutors {

    public static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolExecutors.class);

    private static final Map<String, ExecutorService> poolMap = new ConcurrentHashMap<>();

    private static final Map<String, ScheduledExecutorService> scheduledMap = new ConcurrentHashMap<>();

    public static ExecutorService pool(String name, int threads, int maxThreads, long keepsAliveMills, boolean daemon,
            RejectedExecutionHandler handler) {
        return poolMap.computeIfAbsent(name, (k) -> new ThreadPoolExecutor(threads, maxThreads, keepsAliveMills, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CoreThreadFactory(name, daemon), handler));
    }

    public static ExecutorService pool(String name, int threads, int maxThreads, long keepsAliveMills, boolean daemon) {
        return pool(name, threads, maxThreads, keepsAliveMills, daemon, new AbortPolicy());
    }

    public static ExecutorService pool(String name, int threads, int maxThreads, long keepsAliveMills) {
        return pool(name, threads, maxThreads, keepsAliveMills, false, new AbortPolicy());
    }

    public static ExecutorService pool(String name, int threads, int maxThreads) {
        return pool(name, threads, maxThreads, 60000L, false, new AbortPolicy());
    }

    public static ExecutorService pool(String name, int threads) {
        return pool(name, threads, threads, 60000L, false, new AbortPolicy());
    }

    public static ScheduledExecutorService scheduled(String name, int threads, boolean daemon, RejectedExecutionHandler handler) {
        return scheduledMap.computeIfAbsent(name, (k) -> new ScheduledThreadPoolExecutor(threads, new CoreThreadFactory(name, daemon), handler));
    }

    public static ScheduledExecutorService scheduled(String name, int threads, boolean daemon) {
        return scheduled(name, threads, daemon, new AbortPolicy());
    }

    public static ScheduledExecutorService scheduled(String name, int threads) {
        return scheduled(name, threads, false);
    }

}
