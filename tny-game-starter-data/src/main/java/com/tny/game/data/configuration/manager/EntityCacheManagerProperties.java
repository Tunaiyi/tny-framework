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

    private boolean enable = true;

    private String cacheKeyMakerFactory = AnnotationCacheKeyMakerFactory.MAKER_NAME;

    private String cacheFactory = LocalObjectCacheFactory.CACHE_NAME;

    private String storageFactory = QueueObjectStorageFactory.STORAGE_NAME;

    public boolean isEnable() {
        return enable;
    }

    public EntityCacheManagerProperties setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public String getCacheKeyMakerFactory() {
        return cacheKeyMakerFactory;
    }

    public EntityCacheManagerProperties setCacheKeyMakerFactory(String cacheKeyMakerFactory) {
        this.cacheKeyMakerFactory = cacheKeyMakerFactory;
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
