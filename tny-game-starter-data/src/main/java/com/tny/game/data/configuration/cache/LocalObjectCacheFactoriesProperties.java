package com.tny.game.data.configuration.cache;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
@ConfigurationProperties(prefix = "tny.data.cache")
public class LocalObjectCacheFactoriesProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private LocalObjectCacheFactorySetting localCache = new LocalObjectCacheFactorySetting();

	private Map<String, LocalObjectCacheFactorySetting> localCaches = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public LocalObjectCacheFactoriesProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public LocalObjectCacheFactorySetting getLocalCache() {
		return localCache;
	}

	public LocalObjectCacheFactoriesProperties setLocalCache(LocalObjectCacheFactorySetting localCache) {
		this.localCache = localCache;
		return this;
	}

	public LocalObjectCacheFactorySetting getCache(String name) {
		if (name.isEmpty()) {
			return this.localCache;
		}
		return localCaches.get(name);
	}

	public Map<String, LocalObjectCacheFactorySetting> getLocalCaches() {
		return localCaches;
	}

	public LocalObjectCacheFactoriesProperties setLocalCaches(Map<String, LocalObjectCacheFactorySetting> localCaches) {
		this.localCaches = localCaches;
		return this;
	}

}
