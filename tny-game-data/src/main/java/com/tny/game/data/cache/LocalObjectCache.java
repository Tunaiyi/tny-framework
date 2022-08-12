/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.cache;

import com.google.common.cache.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;

/**
 * <p>
 */
public class LocalObjectCache<K extends Comparable<?>, O> implements ObjectCache<K, O>, RecyclableCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalObjectCache.class);

    /**
     * 缓存
     */
    private Cache<K, CacheEntry<K, O>> cache;

    /**
     * 缓存方案
     */
    private EntityScheme scheme;

    /**
     * 回收策略工厂
     */
    private ReleaseStrategyFactory<K, O> releaseStrategyFactory;

    public LocalObjectCache() {
    }

    public LocalObjectCache(EntityScheme scheme, ReleaseStrategyFactory<K, O> releaseStrategyFactory) {
        this.scheme = scheme;
        this.releaseStrategyFactory = releaseStrategyFactory;
        this.cache = CacheBuilder.newBuilder()
                .concurrencyLevel(scheme.concurrencyLevel())
                .build();

        CacheBuilder<Object, Object> build = CacheBuilder.newBuilder();
        build.concurrencyLevel(scheme.concurrencyLevel());
        this.setValue(scheme.maxCacheSize(), v -> v > 0, build::maximumSize);
        this.cache = build.build();
    }

    @Override
    public EntityScheme getScheme() {
        return scheme;
    }

    @Override
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

    @Override
    public void put(K key, O value) {
        CacheEntry<K, O> old = cache.getIfPresent(key);
        if (old != null) {
            old.replace(value);
        } else {
            CacheEntry<K, O> entry = new CacheEntry<>(key, value, releaseStrategyFactory.createStrategy(key, value));
            CacheEntry<K, O> existOne = cache.asMap().putIfAbsent(key, entry);
            if (existOne != null) {
                existOne.replace(value);
            }
        }
    }

    @Override
    public boolean remove(K key, O object) {
        Asserts.checkNotNull(object, "object {} is null", key);
        CacheEntry<K, O> entry = cache.getIfPresent(key);
        if (entry != null && entry.remove(object)) {
            cache.asMap().remove(key, entry);
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        return (int)cache.size();
    }

    @Override
    public void recycle() {
        long size = this.cache.size();
        int removeSize = 0;
        long releaseAt = System.currentTimeMillis();
        ConcurrentMap<K, CacheEntry<K, O>> entries = this.cache.asMap();
        for (Entry<K, CacheEntry<K, O>> entry : entries.entrySet()) {
            try {
                if (entry.getValue().release(releaseAt)) {
                    if (entries.remove(entry.getKey(), entry.getValue())) {
                        removeSize++;
                    }
                    LOGGER.debug("{} Cache 释放 {} ", this.scheme.getEntityClass(), entry.getKey());
                }
            } catch (Throwable e) {
                LOGGER.error("{} Cache {} 回收异常", this.scheme.getEntityClass(), entry.getKey(), e);
            }
        }
        LOGGER.info("{} 对象池中存在 {} 个对象, 释放 {} 个对象, 剩余 {} 个对象",
                this.scheme.getEntityClass(), size, removeSize, this.cache.size());
    }

    protected LocalObjectCache<K, O> setScheme(EntityScheme scheme) {
        this.scheme = scheme;
        CacheBuilder<Object, Object> build = CacheBuilder.newBuilder();
        build.concurrencyLevel(scheme.concurrencyLevel());
        this.setValue(scheme.maxCacheSize(), v -> v > 0, build::maximumSize);
        this.cache = build.build();
        return this;
    }

    protected LocalObjectCache<K, O> setReleaseStrategyFactory(ReleaseStrategyFactory<K, O> releaseStrategyFactory) {
        this.releaseStrategyFactory = releaseStrategyFactory;
        return this;
    }

    private <T> void setValue(T value, Predicate<T> condition, Consumer<T> setter) {
        if (condition.test(value)) {
            setter.accept(value);
        }
    }

}
