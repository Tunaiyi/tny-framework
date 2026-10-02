/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.data.storage;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;

import static com.tny.game.common.lifecycle.unit.UnitNames.*;
import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 4:53 下午
 */
public class QueueObjectStorageFactory extends AbstractCachedFactory<Class<?>, ObjectStorage<?, ?>> implements ObjectStorageFactory {

    public static final String STORAGE_NAME = lowerCamelName(QueueObjectStorageFactory.class);

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
