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
        // 键含参数组：原仅按名 computeIfAbsent 使同名不同参静默共用首建池
        String key = poolKey(name, threads, maxThreads, keepsAliveMills, daemon, handler);
        return poolMap.computeIfAbsent(key, (k) -> new ThreadPoolExecutor(threads, maxThreads, keepsAliveMills, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CoreThreadFactory(name, daemon), handler));
    }

    private static String poolKey(String name, int threads, int maxThreads, long keepsAliveMills, boolean daemon,
            RejectedExecutionHandler handler) {
        return name + '#' + threads + '/' + maxThreads + '/' + keepsAliveMills + '/' + daemon + '/' + handler.getClass().getName();
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
        String key = name + '#' + threads + '/' + daemon + '/' + handler.getClass().getName();
        return scheduledMap.computeIfAbsent(key, (k) -> new ScheduledThreadPoolExecutor(threads, new CoreThreadFactory(name, daemon), handler));
    }

    /**
     * 整体关闭注册表内全部池并清空注册（幂等；关闭后同参数申请将新建）。
     */
    public static synchronized void shutdownAll() {
        poolMap.values().forEach(ExecutorService::shutdownNow);
        poolMap.clear();
        scheduledMap.values().forEach(ExecutorService::shutdownNow);
        scheduledMap.clear();
    }

    public static ScheduledExecutorService scheduled(String name, int threads, boolean daemon) {
        return scheduled(name, threads, daemon, new AbortPolicy());
    }

    public static ScheduledExecutorService scheduled(String name, int threads) {
        return scheduled(name, threads, false);
    }

}
