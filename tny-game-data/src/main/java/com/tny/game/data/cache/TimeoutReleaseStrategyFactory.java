package com.tny.game.data.cache;

/**
 * <p>
 */
public class TimeoutReleaseStrategyFactory<K extends Comparable<K>, O> implements ReleaseStrategyFactory<K, O> {

	private final TimeoutReleaseStrategySetting setting;

	public TimeoutReleaseStrategyFactory(TimeoutReleaseStrategySetting setting) {
		this.setting = setting;
	}

	@Override
	public ReleaseStrategy<K, O> createStrategy(K key, O object) {
		return new TimeoutReleaseStrategy<>(this.setting.getLife());
	}

}
