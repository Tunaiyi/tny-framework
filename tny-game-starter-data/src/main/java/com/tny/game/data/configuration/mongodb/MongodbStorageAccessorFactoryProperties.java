package com.tny.game.data.configuration.mongodb;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor.mongodb-accessor")
public class MongodbStorageAccessorFactoryProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private MongodbStorageAccessorFactorySetting accessor = new MongodbStorageAccessorFactorySetting();

	private Map<String, MongodbStorageAccessorFactorySetting> accessors = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public MongodbStorageAccessorFactoryProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public MongodbStorageAccessorFactorySetting getAccessor() {
		return accessor;
	}

	public MongodbStorageAccessorFactoryProperties setAccessor(MongodbStorageAccessorFactorySetting accessor) {
		this.accessor = accessor;
		return this;
	}

	public Map<String, MongodbStorageAccessorFactorySetting> getAccessors() {
		return accessors;
	}

	public MongodbStorageAccessorFactoryProperties setAccessors(
			Map<String, MongodbStorageAccessorFactorySetting> accessors) {
		this.accessors = accessors;
		return this;
	}

}
