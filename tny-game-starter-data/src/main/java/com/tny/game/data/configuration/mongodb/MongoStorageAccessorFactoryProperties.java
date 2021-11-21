package com.tny.game.data.configuration.mongodb;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor.mongo-accessor")
public class MongoStorageAccessorFactoryProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private MongoStorageAccessorFactorySetting accessor = new MongoStorageAccessorFactorySetting();

	private Map<String, MongoStorageAccessorFactorySetting> accessors = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public MongoStorageAccessorFactoryProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public MongoStorageAccessorFactorySetting getAccessor() {
		return accessor;
	}

	public MongoStorageAccessorFactoryProperties setAccessor(MongoStorageAccessorFactorySetting accessor) {
		this.accessor = accessor;
		return this;
	}

	public Map<String, MongoStorageAccessorFactorySetting> getAccessors() {
		return accessors;
	}

	public MongoStorageAccessorFactoryProperties setAccessors(
			Map<String, MongoStorageAccessorFactorySetting> accessors) {
		this.accessors = accessors;
		return this;
	}

}
