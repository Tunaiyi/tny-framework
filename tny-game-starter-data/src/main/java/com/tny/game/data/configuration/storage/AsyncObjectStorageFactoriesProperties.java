/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
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
