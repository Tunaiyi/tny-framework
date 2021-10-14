package com.tny.game.data.configuration.cache;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
@ConfigurationProperties(prefix = "tny.data.object-cache.local-cache")
public class LocalObjectCacheFactoriesProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private LocalObjectCacheFactorySetting cache = new LocalObjectCacheFactorySetting();

	private Map<String, LocalObjectCacheFactorySetting> caches = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public LocalObjectCacheFactoriesProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public LocalObjectCacheFactorySetting getCache() {
		return cache;
	}

	public LocalObjectCacheFactoriesProperties setCache(LocalObjectCacheFactorySetting cache) {
		this.cache = cache;
		return this;
	}

	public LocalObjectCacheFactorySetting getCache(String name) {
		if (name.isEmpty()) {
			return this.cache;
		}
		return caches.get(name);
	}

	public Map<String, LocalObjectCacheFactorySetting> getCaches() {
		return caches;
	}

	public LocalObjectCacheFactoriesProperties setCaches(Map<String, LocalObjectCacheFactorySetting> caches) {
		this.caches = caches;
		return this;
	}

}
