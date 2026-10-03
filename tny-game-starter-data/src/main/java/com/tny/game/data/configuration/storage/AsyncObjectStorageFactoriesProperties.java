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
public class AsyncObjectStorageFactoriesProperties
        extends AbstractObjectStorageFactoriesProperties<QueueObjectStorageFactorySetting> {

    public AsyncObjectStorageFactoriesProperties() {
        super(new QueueObjectStorageFactorySetting());
    }

    @Override
    public AsyncObjectStorageFactoriesProperties setEnable(boolean enable) {
        super.setEnable(enable);
        return this;
    }

    @Override
    public QueueObjectStorageFactorySetting getStorage() {
        return super.getStorage();
    }

    @Override
    public AsyncObjectStorageFactoriesProperties setStorage(QueueObjectStorageFactorySetting storage) {
        super.setStorage(storage);
        return this;
    }

    @Override
    public AsyncObjectStorageFactoriesProperties setStorages(Map<String, QueueObjectStorageFactorySetting> storages) {
        super.setStorages(storages);
        return this;
    }

}
