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

package com.tny.game.data.cache;

import com.tny.game.data.annotation.*;
import org.apache.commons.lang3.StringUtils;

/**
 * 实体方案
 * <p>
 */
public class EntityScheme {

    /**
     * 实体类
     */
    private final Class<?> entityClass;

    /**
     * 视图配置
     */
    private final EntityObject entityObject;

    public EntityScheme(Class<?> entityClass) {
        this.entityClass = entityClass;
        this.entityObject = entityClass.getAnnotation(EntityObject.class);
    }

    public String prefix() {
        return entityObject.prefix();
    }

    public boolean isHasPrefix() {
        return StringUtils.isNoneBlank(entityObject.prefix());
    }

    public String cacheFactory() {
        return entityObject.cacheFactory();
    }

    public String storageFactory() {
        return entityObject.storageFactory();
    }

    public String keyMakerFactory() {
        return entityObject.keyMakerFactory();
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public Class<?> getCacheClass() {
        return entityObject.cache() == Self.class ? entityClass : entityObject.cache();
    }

    public boolean isCacheSelf() {
        return entityObject.cache() == Self.class;
    }

    public long maxCacheSize() {
        return this.entityObject.maxCacheSize();
    }

    public int concurrencyLevel() {
        return this.entityObject.concurrencyLevel();
    }

}
