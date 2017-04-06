package com.tny.game.suite.base;

import com.tny.game.suite.cache.GameCacheDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 并发读取Manager
 * Created by Kun Yang on 16/4/22.
 */
public abstract class ParallelismLoadManager<O> extends GameCacheManager<O> {

    private String tableHead;

    private int groupSize;

    private static final ForkJoinPool forkJoinPool = ForkJoinPool.getCommonPoolParallelism() >= 100 ?
            ForkJoinPool.commonPool() : new ForkJoinPool(20);

    private boolean parallelism;

    @Autowired
    private GameCacheDAO cacheDAO;

    protected ParallelismLoadManager(Class<? extends O> entityClass, String tableHead) {
        this(entityClass, null, tableHead, 75);
    }

    protected ParallelismLoadManager(Class<? extends O> entityClass, String tableHead, int groupSize) {
        super(entityClass, null);
        this.tableHead = tableHead;
        this.groupSize = groupSize <= 0 ? 75 : groupSize;
    }

    protected ParallelismLoadManager(Class<? extends O> entityClass, Consumer<O> consumer, String tableHead) {
        this(entityClass, consumer, tableHead, 75);
    }

    protected ParallelismLoadManager(Class<? extends O> entityClass, Consumer<O> consumer, String tableHead, int groupSize) {
        super(entityClass, consumer);
        this.tableHead = tableHead;
        this.groupSize = groupSize <= 0 ? 75 : groupSize;
    }

    protected Collection<O> getObjects(long playerID) {
        List<String> keys = cacheDAO.getKeys(playerID, this.tableHead);
        if (keys.isEmpty())
            return Collections.emptyList();
        return gets(keys, false);
    }

    protected Collection<O> getAllObjects() {
        List<String> keys = cacheDAO.getAllKeys(this.tableHead);
        if (keys.isEmpty())
            return Collections.emptyList();
        return gets(keys, false);
    }

    protected Collection<O> getObjects(long playerID, boolean parallelism) {
        List<String> keys = cacheDAO.getKeys(playerID, this.tableHead);
        if (keys.isEmpty())
            return Collections.emptyList();
        return gets(keys, parallelism);
    }

    protected Collection<O> getAllObjects(boolean parallelism) {
        List<String> keys = cacheDAO.getAllKeys(this.tableHead);
        if (keys.isEmpty())
            return Collections.emptyList();
        return gets(keys, parallelism);
    }

    private Collection<O> doGet(Collection<String> keys, boolean parallelism) {
        List<List<String>> keysList = keys.parallelStream()
                .collect(() -> {
                            List<List<String>> newList = new ArrayList<>();
                            newList.add(new ArrayList<>());
                            return newList;
                        },
                        (list, v) -> {
                            List<String> lastList = list.get(list.size() - 1);
                            if (lastList.size() >= groupSize) {
                                lastList = new ArrayList<>();
                                list.add(lastList);
                            }
                            lastList.add(v);
                        },
                        List::addAll);
        Stream<List<String>> keySteam = parallelism ? keysList.parallelStream() : keysList.stream();
        return keySteam
                .map(this::getByKeys)
                .reduce(new ConcurrentLinkedQueue<>(), (total, value) -> {
                    if (total != value)
                        total.addAll(value);
                    return total;
                });
    }

    protected Collection<O> gets(Collection<String> keys) {
        return gets(keys, false);
    }

    protected Collection<O> gets(Collection<String> keys, boolean parallelism) {
        if (parallelism)
            return forkJoinPool.submit(() -> doGet(keys, true)).join();
        else
            return doGet(keys, false);
    }

}
