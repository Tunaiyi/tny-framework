package com.tny.game.data.configuration.storage;

import org.springframework.boot.context.properties.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/28 2:37 下午
 */
@ConfigurationProperties(prefix = "tny.data.storage")
public class QueueObjectStorageFactoriesProperties {

	private boolean enable = true;

	@NestedConfigurationProperty
	private QueueObjectStorageFactorySetting queueStorage = new QueueObjectStorageFactorySetting();

	private Map<String, QueueObjectStorageFactorySetting> queueStorages = new HashMap<>();

	public boolean isEnable() {
		return enable;
	}

	public QueueObjectStorageFactoriesProperties setEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	public QueueObjectStorageFactorySetting getQueueStorage() {
		return queueStorage;
	}

	public QueueObjectStorageFactoriesProperties setQueueStorage(QueueObjectStorageFactorySetting queueStorage) {
		this.queueStorage = queueStorage;
		return this;
	}

	public Map<String, QueueObjectStorageFactorySetting> getQueueStorages() {
		return queueStorages;
	}

	public QueueObjectStorageFactoriesProperties setQueueStorages(
			Map<String, QueueObjectStorageFactorySetting> queueStorages) {
		this.queueStorages = queueStorages;
		return this;
	}

}
