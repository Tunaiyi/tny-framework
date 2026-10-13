/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
