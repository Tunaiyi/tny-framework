package com.tny.game.data.configuration.mongodb;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage-accessor.mongo-template-accessor")
public class MongoTemplateStorageAccessorFactoryProperties {

	private boolean enable = false;

	@NestedConfigurationProperty
	private MongoTemplateStorageAccessorFactorySetting accessor = new MongoTemplateStorageAccessorFactorySetting();

	private Map<String, MongoTemplateStorageAccessorFactorySetting> accessors = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public MongoTemplateStorageAccessorFactoryProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public MongoTemplateStorageAccessorFactorySetting getAccessor() {
		return accessor;
	}

	public MongoTemplateStorageAccessorFactoryProperties setAccessor(MongoTemplateStorageAccessorFactorySetting accessor) {
		this.accessor = accessor;
		return this;
	}

	public Map<String, MongoTemplateStorageAccessorFactorySetting> getAccessors() {
		return accessors;
	}

	public MongoTemplateStorageAccessorFactoryProperties setAccessors(
			Map<String, MongoTemplateStorageAccessorFactorySetting> accessors) {
		this.accessors = accessors;
		return this;
	}

}
