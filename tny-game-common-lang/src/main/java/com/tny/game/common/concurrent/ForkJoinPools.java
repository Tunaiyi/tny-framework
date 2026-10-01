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
