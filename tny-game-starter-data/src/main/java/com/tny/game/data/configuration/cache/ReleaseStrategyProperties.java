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
@ConfigurationProperties(prefix = "tny.data.cache.release-strategy")
public class ReleaseStrategyProperties {

	@NestedConfigurationProperty
	private TimeoutReleaseStrategySetting timeoutStrategy = new TimeoutReleaseStrategySetting();

	private Map<String, TimeoutReleaseStrategySetting> timeoutStrategies = new HashMap<>();

	public TimeoutReleaseStrategySetting getTimeoutStrategy() {
		return timeoutStrategy;
	}

	public ReleaseStrategyProperties setTimeoutStrategy(TimeoutReleaseStrategySetting timeoutStrategy) {
		this.timeoutStrategy = timeoutStrategy;
		return this;
	}

	public Map<String, TimeoutReleaseStrategySetting> getTimeoutStrategies() {
		return timeoutStrategies;
	}

	public ReleaseStrategyProperties setTimeoutStrategies(Map<String, TimeoutReleaseStrategySetting> timeoutStrategies) {
		this.timeoutStrategies = timeoutStrategies;
		return this;
	}

}
