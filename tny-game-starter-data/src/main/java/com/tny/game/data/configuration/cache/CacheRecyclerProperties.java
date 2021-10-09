package com.tny.game.data.configuration.cache;

import org.springframework.boot.context.properties.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
@ConfigurationProperties(prefix = "tny.data.cache.recycler")
public class CacheRecyclerProperties {

	@NestedConfigurationProperty
	private ScheduledCacheRecyclerSetting scheduledCacheRecycler = new ScheduledCacheRecyclerSetting();

	public ScheduledCacheRecyclerSetting getScheduledCacheRecycler() {
		return scheduledCacheRecycler;
	}

	public CacheRecyclerProperties setScheduledCacheRecycler(ScheduledCacheRecyclerSetting scheduledCacheRecycler) {
		this.scheduledCacheRecycler = scheduledCacheRecycler;
		return this;
	}

}
