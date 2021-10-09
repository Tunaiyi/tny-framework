package com.tny.game.data.storage;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 4:53 下午
 */
public class QueueObjectStorageFactory extends AbstractCachedFactory<Class<?>, ObjectStorage<?, ?>> implements ObjectStorageFactory {

	public static final String STORAGE_NAME = "queueObjectCacheFactory";

	private AsyncObjectStoreExecutor storeExecutor;

	private StorageAccessorFactory accessorFactory;

	public QueueObjectStorageFactory() {
	}

	public QueueObjectStorageFactory(AsyncObjectStoreExecutor storeExecutor,
			StorageAccessorFactory accessorFactory) {
		this.storeExecutor = storeExecutor;
		this.accessorFactory = accessorFactory;
	}

	public QueueObjectStorageFactory setStoreExecutor(AsyncObjectStoreExecutor storeExecutor) {
		this.storeExecutor = storeExecutor;
		return this;
	}

	public QueueObjectStorageFactory setAccessorFactory(StorageAccessorFactory accessorFactory) {
		this.accessorFactory = accessorFactory;
		return this;
	}

	@Override
	public <K extends Comparable<?>, O> ObjectStorage<K, O> createStorage(CacheScheme scheme, EntityKeyMaker<K, O> keyMaker) {
		return loadOrCreate(scheme.getEntityClass(), (key) -> {
			StorageAccessor<K, O> accessor = accessorFactory.createAccessor(scheme, keyMaker);
			AsyncObjectStorage<K, O> storage = new QueueObjectStorage<>(
					as(scheme.getEntityClass()), accessor, new HashObjectLocker<>(scheme.concurrencyLevel()));
			storeExecutor.register(storage);
			return storage;
		});
	}

}
