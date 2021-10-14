package com.tny.game.data.configuration.storage;

import com.tny.game.boot.utils.*;
import com.tny.game.data.storage.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/28 2:37 下午
 */
public class QueueObjectStorageFactorySetting {

	private String storeExecutor = BeanNameUtils.lowerCamelName(ForkJoinAsyncObjectStoreExecutor.class);

	private String accessorFactory;

	public String getStoreExecutor() {
		return storeExecutor;
	}

	public QueueObjectStorageFactorySetting setStoreExecutor(String storeExecutor) {
		this.storeExecutor = storeExecutor;
		return this;
	}

	public String getAccessorFactory() {
		return accessorFactory;
	}

	public QueueObjectStorageFactorySetting setAccessorFactory(String accessorFactory) {
		this.accessorFactory = accessorFactory;
		return this;
	}

}
