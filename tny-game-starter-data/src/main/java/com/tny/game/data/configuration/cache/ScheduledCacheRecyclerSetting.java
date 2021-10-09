package com.tny.game.data.configuration.cache;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 11:33 上午
 */
public class ScheduledCacheRecyclerSetting {

	private int recycleIntervalTime = 15000;

	public int getRecycleIntervalTime() {
		return recycleIntervalTime;
	}

	public ScheduledCacheRecyclerSetting setRecycleIntervalTime(int recycleIntervalTime) {
		this.recycleIntervalTime = recycleIntervalTime;
		return this;
	}

}
