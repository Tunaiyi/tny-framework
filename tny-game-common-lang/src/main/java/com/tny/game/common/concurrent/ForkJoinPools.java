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

/**
 * Created by Kun Yang on 2017/11/3.
 */
public class ForkJoinPools {

    public static final Logger LOGGER = LoggerFactory.getLogger(ForkJoinPools.class);

    private static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private static final Map<String, ForkJoinPool> poolMap = new ConcurrentHashMap<>();

    public static ForkJoinPool pool(int threads, String name, boolean asyncMode) {
        // 键含参数组（net 命令执行器以类简名为键申请不同线程数——静默共用首建池是真实受害路径）
        String key = name + '#' + threads + '/' + asyncMode;
        return poolMap.computeIfAbsent(key, (k) -> new ForkJoinPool(threads, new CoreThreadFactory(name),
                (t, e) -> LOGGER.error("{} 运行异常", name, e), asyncMode));
    }

    /**
     * 整体关闭注册表内全部池并清空注册（幂等；关闭后同参数申请将新建）。
     */
    public static synchronized void shutdownAll() {
        poolMap.values().forEach(ForkJoinPool::shutdownNow);
        poolMap.clear();
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
