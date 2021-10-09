package com.tny.game.data.configuration.redisson;

import com.tny.game.data.redisson.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor")
@ConditionalOnClass(RedissonStorageAccessorFactory.class)
public class RedissonStorageAccessorFactoryProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private RedissonStorageAccessorFactorySetting redissonAccessor = new RedissonStorageAccessorFactorySetting();

	private Map<String, RedissonStorageAccessorFactorySetting> redissonAccessors = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public RedissonStorageAccessorFactoryProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public RedissonStorageAccessorFactorySetting getRedissonAccessor() {
		return redissonAccessor;
	}

	public RedissonStorageAccessorFactoryProperties setRedissonAccessor(RedissonStorageAccessorFactorySetting redissonAccessor) {
		this.redissonAccessor = redissonAccessor;
		return this;
	}

	public Map<String, RedissonStorageAccessorFactorySetting> getRedissonAccessors() {
		return redissonAccessors;
	}

	public RedissonStorageAccessorFactoryProperties setRedissonAccessors(
			Map<String, RedissonStorageAccessorFactorySetting> redissonAccessors) {
		this.redissonAccessors = redissonAccessors;
		return this;
	}

}
