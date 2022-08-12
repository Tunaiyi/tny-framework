/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.configuration.manager;

import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 8:46 下午
 */
@ConfigurationProperties(prefix = "tny.data.entity-manager")
public class EntityCacheManagerProperties {

    private String keyMakerFactory = AnnotationEntityKeyMakerFactory.MAKER_NAME;

    private String cacheFactory = LocalObjectCacheFactory.CACHE_NAME;

    private String storageFactory = QueueObjectStorageFactory.STORAGE_NAME;

    public String getKeyMakerFactory() {
        return keyMakerFactory;
    }

    public EntityCacheManagerProperties setKeyMakerFactory(String keyMakerFactory) {
        this.keyMakerFactory = keyMakerFactory;
        return this;
    }

    public String getCacheFactory() {
        return cacheFactory;
    }

    public EntityCacheManagerProperties setCacheFactory(String cacheFactory) {
        this.cacheFactory = cacheFactory;
        return this;
    }

    public String getStorageFactory() {
        return storageFactory;
    }

    public EntityCacheManagerProperties setStorageFactory(String storageFactory) {
        this.storageFactory = storageFactory;
        return this;
    }

}
