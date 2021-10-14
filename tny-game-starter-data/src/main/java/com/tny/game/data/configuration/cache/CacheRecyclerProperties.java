package com.tny.game.data.configuration.cache;

import org.springframework.boot.context.properties.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/17 5:21 下午
 */
@ConfigurationProperties(prefix = "tny.data.object-cache.recycler.scheduled-recycler")
public class CacheRecyclerProperties {

	@NestedConfigurationProperty
	private ScheduledCacheRecyclerSetting scheduled = new ScheduledCacheRecyclerSetting();

	public ScheduledCacheRecyclerSetting getScheduled() {
		return scheduled;
	}

	public CacheRecyclerProperties setScheduled(ScheduledCacheRecyclerSetting scheduled) {
		this.scheduled = scheduled;
		return this;
	}

}
