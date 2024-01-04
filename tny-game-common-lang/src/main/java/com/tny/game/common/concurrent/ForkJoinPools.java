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
