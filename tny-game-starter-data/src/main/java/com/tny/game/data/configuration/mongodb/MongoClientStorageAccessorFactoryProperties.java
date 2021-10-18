package com.tny.game.data.configuration.mongodb;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor.mongo-client-accessor")
public class MongoClientStorageAccessorFactoryProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private MongoClientStorageAccessorFactorySetting accessor = new MongoClientStorageAccessorFactorySetting();

	private Map<String, MongoClientStorageAccessorFactorySetting> accessors = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public MongoClientStorageAccessorFactoryProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public MongoClientStorageAccessorFactorySetting getAccessor() {
		return accessor;
	}

	public MongoClientStorageAccessorFactoryProperties setAccessor(MongoClientStorageAccessorFactorySetting accessor) {
		this.accessor = accessor;
		return this;
	}

	public Map<String, MongoClientStorageAccessorFactorySetting> getAccessors() {
		return accessors;
	}

	public MongoClientStorageAccessorFactoryProperties setAccessors(
			Map<String, MongoClientStorageAccessorFactorySetting> accessors) {
		this.accessors = accessors;
		return this;
	}

}
