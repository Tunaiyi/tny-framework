package com.tny.game.data.cache;

import com.google.common.cache.*;
import com.tny.game.common.concurrent.*;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * <p>
 */
public class DefaultObjectCache<K extends Comparable<K>, O> {//implements ObjectCache<K, O> {

    /**
     * 释放策略工厂
     */
    // private ObjectReleaseStrategyFactory<K, O> releaseStrategyFactory;

    /**
     * 回收异步对象线程
     */
    private static final ScheduledExecutorService recycleExecutor = Executors
            .newScheduledThreadPool(1, new CoreThreadFactory("AutoObjectCache-RecycleExecutor", false));

    /**
     * 缓存
     */
    private Cache<K, CacheEntry<K, O>> cache;

    /**
     * 缓存方案
     */
    private CacheScheme cacheScheme;

    public DefaultObjectCache(CacheScheme cacheScheme) {
        CacheBuilder<Object, Object> build = CacheBuilder.newBuilder();
        build.concurrencyLevel(cacheScheme.concurrencyLevel());
        this.setValue(cacheScheme.maxCacheSize(), v -> v > 0, build::maximumSize);
        // this.setValue(cacheScheme.expireAfterAccess(), v -> v > 0, v -> build.expireAfterAccess(Duration.ofMillis(v)));
        // this.setValue(cacheScheme.expireAfterWrite(), v -> v > 0, v -> build.expireAfterWrite(Duration.ofMillis(v)));
    }

    private <T> void setValue(T value, Predicate<T> condition, Consumer<T> setter) {
        if (condition.test(value))
            setter.accept(value);
    }

    // @Override
    public O get(K key) {
        CacheEntry<K, O> entity = cache.getIfPresent(key);
        if (entity != null) {
            if (entity.tryVisit()) {
                return entity.getObject();
            } else {
                cache.asMap().remove(key, entity);
            }
        }
        return null;
    }

    // @Override
    public void put(K key, O value) {
        CacheEntry<K, O> old = cache.asMap().get(key);
        if (old != null) {
            if (old.replace(key, value))
                return;
        }
        // CacheEntry<K, O> entity = new CacheEntry<>(key, value, false, releaseStrategyFactory.createStrategy(key, value));
        // cache.put(key, entity);
    }

    // @Override
    public void remove(K key) {
        cache.invalidate(key);
    }

    // @Override
    public long size() {
        return cache.size();
    }

}
