/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.tny.game.data.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * 并发读取Manager
 * Created by Kun Yang on 16/4/22.
 */
@Deprecated
public abstract class ParallelLoadGameCacheManager<O> extends GameCacheManager<O> {

    private final int groupSize;

    private static final ForkJoinPool forkJoinPool = ForkJoinPool.getCommonPoolParallelism() >= 100 ?
            ForkJoinPool.commonPool() : new ForkJoinPool(20);

    protected ParallelLoadGameCacheManager(Class<? extends O> entityClass, EntityCacheManager<AnyId, O> manager) {
        this(entityClass, manager, 75);
    }

    protected ParallelLoadGameCacheManager(Class<? extends O> entityClass, EntityCacheManager<AnyId, O> manager, int groupSize) {
        super(entityClass, manager, null);
        this.groupSize = groupSize <= 0 ? 75 : groupSize;
    }

    protected ParallelLoadGameCacheManager(Class<? extends O> entityClass, EntityCacheManager<AnyId, O> manager,
            EntityOnLoad<AnyId, O> onLoad) {
        this(entityClass, manager, onLoad, 75);
    }

    protected ParallelLoadGameCacheManager(Class<? extends O> entityClass, EntityCacheManager<AnyId, O> manager, EntityOnLoad<AnyId, O> onLoad,
            int groupSize) {
        super(entityClass, manager, onLoad);
        this.groupSize = groupSize <= 0 ? 75 : groupSize;
    }

    protected List<O> getObjects(long playerId) {
        List<AnyId> keys = this.findIdList(playerId);
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return get(keys, false);
    }

    protected List<O> getAllObjects() {
        List<AnyId> keys = this.findAllIdList();
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return get(keys, false);
    }

    protected List<O> getObjects(long playerId, boolean parallelism) {
        List<AnyId> keys = this.findIdList(playerId);
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return get(keys, parallelism);
    }

    protected List<O> get(Collection<AnyId> keys, boolean parallelism) {
        if (parallelism) {
            return forkJoinPool.submit(() -> doGet(keys, true)).join();
        } else {
            return doGet(keys, false);
        }
    }

    protected List<O> getAllObjects(boolean parallelism) {
        List<AnyId> keys = this.findAllIdList();
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        return get(keys, parallelism);
    }

    private List<O> doGet(Collection<AnyId> keys, boolean parallelism) {
        List<List<AnyId>> keysList = keys.parallelStream()
                .collect(() -> {
                            List<List<AnyId>> newList = new ArrayList<>();
                            newList.add(new ArrayList<>());
                            return newList;
                        },
                        (list, v) -> {
                            List<AnyId> lastList = list.get(list.size() - 1);
                            if (lastList.size() >= this.groupSize) {
                                lastList = new ArrayList<>();
                                list.add(lastList);
                            }
                            lastList.add(v);
                        },
                        List::addAll);
        Stream<List<AnyId>> keySteam = parallelism ? keysList.parallelStream() : keysList.stream();
        return new ArrayList<>(keySteam
                .map(this::getByKeys)
                .reduce(new ConcurrentLinkedQueue<>(), (total, value) -> {
                    if (total != value) {
                        total.addAll(value);
                    }
                    return total;
                }));
    }

}
