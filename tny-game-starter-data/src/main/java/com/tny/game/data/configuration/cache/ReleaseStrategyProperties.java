package com.tny.game.data.configuration.cache;

import com.tny.game.data.cache.*;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 11:27 上午
 */
@ConfigurationProperties(prefix = "tny.data.object-cache.release.timeout-strategy")
public class ReleaseStrategyProperties {

	@NestedConfigurationProperty
	private TimeoutReleaseStrategySetting strategy = new TimeoutReleaseStrategySetting();

	private Map<String, TimeoutReleaseStrategySetting> strategies = new HashMap<>();

	public TimeoutReleaseStrategySetting getStrategy() {
		return strategy;
	}

	public ReleaseStrategyProperties setStrategy(TimeoutReleaseStrategySetting strategy) {
		this.strategy = strategy;
		return this;
	}

	public Map<String, TimeoutReleaseStrategySetting> getStrategies() {
		return strategies;
	}

	public ReleaseStrategyProperties setStrategies(Map<String, TimeoutReleaseStrategySetting> strategies) {
		this.strategies = strategies;
		return this;
	}

}
