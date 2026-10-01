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
