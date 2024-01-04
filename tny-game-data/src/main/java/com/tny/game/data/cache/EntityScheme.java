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
