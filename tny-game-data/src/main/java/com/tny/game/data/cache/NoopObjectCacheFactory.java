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

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/26 5:55 下午
 */
public class NoopObjectCacheFactory extends AbstractCachedFactory<Class<?>, ObjectCache<?, ?>> implements ObjectCacheFactory {

    public static final String CACHE_NAME = "noopObjectCacheFactory";

    @Override
    public <K extends Comparable<?>, O> ObjectCache<K, O> createCache(EntityScheme cacheScheme, CacheKeyMaker<K, O> keyMaker) {
        return loadOrCreate(cacheScheme.getEntityClass(), (key) -> new NoopObjectCache<>(cacheScheme));
    }

}
