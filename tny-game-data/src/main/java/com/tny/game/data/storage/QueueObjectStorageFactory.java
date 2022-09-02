/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.data.storage;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

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
    public <K extends Comparable<?>, O> ObjectStorage<K, O> createStorage(EntityScheme scheme, CacheKeyMaker<K, O> keyMaker) {
        return loadOrCreate(scheme.getEntityClass(), (key) -> {
            StorageAccessor<K, O> accessor = accessorFactory.createAccessor(scheme, keyMaker);
            if (!(accessor instanceof AsyncStorageAccessor)) {
                throw new IllegalArgumentException(format("{} accessor 非 {}", accessor.getClass(), AsyncStorageAccessor.class));
            }
            AsyncObjectStorage<K, O> storage = new QueueObjectStorage<>(
                    as(scheme.getEntityClass()), as(accessor), new HashObjectLocker<>(scheme.concurrencyLevel()));
            storeExecutor.register(storage);
            return storage;
        });
    }

}
