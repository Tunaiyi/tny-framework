package com.tny.game.data.configuration.storage;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/28 2:37 下午
 */
@ConfigurationProperties(prefix = "tny.data.object-storage.async-storage")
public class AsyncObjectStorageFactoriesProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private QueueObjectStorageFactorySetting storage = new QueueObjectStorageFactorySetting();

	private Map<String, QueueObjectStorageFactorySetting> storages = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public AsyncObjectStorageFactoriesProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public QueueObjectStorageFactorySetting getStorage() {
		return storage;
	}

	public AsyncObjectStorageFactoriesProperties setStorage(QueueObjectStorageFactorySetting storage) {
		this.storage = storage;
		return this;
	}

	public Map<String, QueueObjectStorageFactorySetting> getStorages() {
		return storages;
	}

	public AsyncObjectStorageFactoriesProperties setStorages(
			Map<String, QueueObjectStorageFactorySetting> storages) {
		this.storages = storages;
		return this;
	}

}
