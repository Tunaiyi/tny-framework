package com.tny.game.data.configuration.manager;

import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 8:46 下午
 */
@ConfigurationProperties(prefix = "tny.data.entity-manager")
public class EntityCacheManagerProperties {

	private String keyMakerFactory = AnnotationEntityKeyMakerFactory.MAKER_NAME;

	private String cacheFactory = LocalObjectCacheFactory.CACHE_NAME;

	private String storageFactory = QueueObjectStorageFactory.STORAGE_NAME;

	public String getKeyMakerFactory() {
		return keyMakerFactory;
	}

	public EntityCacheManagerProperties setKeyMakerFactory(String keyMakerFactory) {
		this.keyMakerFactory = keyMakerFactory;
		return this;
	}

	public String getCacheFactory() {
		return cacheFactory;
	}

	public EntityCacheManagerProperties setCacheFactory(String cacheFactory) {
		this.cacheFactory = cacheFactory;
		return this;
	}

	public String getStorageFactory() {
		return storageFactory;
	}

	public EntityCacheManagerProperties setStorageFactory(String storageFactory) {
		this.storageFactory = storageFactory;
		return this;
	}

}
