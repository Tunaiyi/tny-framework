package com.tny.game.data.cache;

import com.tny.game.data.annotation.*;

/**
 * <p>
 */
public class CacheScheme {

	private final Class<?> entityClass;

	private final EntityObject entityObject;

	public CacheScheme(Class<?> entityClass) {
		this.entityClass = entityClass;
		this.entityObject = entityClass.getAnnotation(EntityObject.class);
	}

	public String cacheFactory() {
		return entityObject.cacheFactory();
	}

	public String storageFactory() {
		return entityObject.storageFactory();
	}

	public String keyMakerFactory() {
		return entityObject.keyMakerFactory();
	}

	public Class<?> getEntityClass() {
		return this.entityClass;
	}

	public Class<?> getCacheClass() {
		return entityObject.cache() == Self.class ? entityClass : entityObject.cache();
	}

	public boolean isCacheSelf() {
		return entityObject.cache() == Self.class;
	}

	public long maxCacheSize() {
		return this.entityObject.maxCacheSize();
	}

	public int concurrencyLevel() {
		return this.entityObject.concurrencyLevel();
	}

}
