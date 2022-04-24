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
    public <K extends Comparable<?>, O> ObjectCache<K, O> createCache(EntityScheme cacheScheme, EntityKeyMaker<K, O> keyMaker) {
        return loadOrCreate(cacheScheme.getEntityClass(), (key) -> new NoopObjectCache<>(cacheScheme));
    }

}
