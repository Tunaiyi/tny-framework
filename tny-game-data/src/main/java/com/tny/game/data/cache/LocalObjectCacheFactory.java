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
	public <K extends Comparable<?>, O> ObjectCache<K, O> createCache(EntityScheme cacheScheme, EntityKeyMaker<K, O> keyMaker) {
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
