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
package com.tny.game.data.cache;

import com.tny.game.data.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/26 5:55 下午
 */
public class LocalObjectCacheFactory extends AbstractCachedFactory<Class<?>, ObjectCache<?, ?>> implements ObjectCacheFactory {

    public static final String CACHE_NAME = "localObjectCacheFactory";

    private CacheRecycler recycler;

    private ReleaseStrategyFactory<?, ?> releaseStrategyFactory;

    @Override
    public <K extends Comparable<?>, O> ObjectCache<K, O> createCache(EntityScheme cacheScheme, CacheKeyMaker<K, O> keyMaker) {
        return loadOrCreate(cacheScheme.getEntityClass(), key -> {
            LocalObjectCache<K, O> cache = new LocalObjectCache<>(cacheScheme, as(releaseStrategyFactory));
            this.recycler.accept(cache);
            return cache;
        });
    }

    public LocalObjectCacheFactory setRecycler(CacheRecycler recycler) {
        this.recycler = recycler;
        return this;
    }

    public LocalObjectCacheFactory setReleaseStrategyFactory(ReleaseStrategyFactory<?, ?> releaseStrategyFactory) {
        this.releaseStrategyFactory = releaseStrategyFactory;
        return this;
    }

}
