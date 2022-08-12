/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public abstract class BaseMongoStorageAccessorFactory extends AbstractCachedFactory<Class<?>, StorageAccessor<?, ?>> implements StorageAccessorFactory {

    protected final String dataSource;

    protected EntityIdConverterFactory entityIdConverterFactory;

    protected MongoEntityConverter entityObjectConverter;

    protected MongoTemplate mongoTemplate;

    public BaseMongoStorageAccessorFactory(String dataSource) {
        this.dataSource = dataSource;
    }

    public BaseMongoStorageAccessorFactory(EntityIdConverterFactory entityIdConverterFactory, MongoEntityConverter entityObjectConverter,
            MongoTemplate mongoTemplate, String dataSource) {
        this.dataSource = dataSource;
        this.entityIdConverterFactory = entityIdConverterFactory;
        this.entityObjectConverter = entityObjectConverter;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
        EntityIdConverter<?, ?, ?> idConverter = entityIdConverterFactory.createConverter(scheme, keyMaker);
        return loadOrCreate(scheme.getEntityClass(), clazz -> this.newMongoStorageAccessor(clazz, idConverter));
    }

    protected abstract StorageAccessor<?, ?> newMongoStorageAccessor(Class<?> entityClass, EntityIdConverter<?, ?, ?> idConverter);

    public BaseMongoStorageAccessorFactory setEntityIdConverterFactory(EntityIdConverterFactory entityIdConverterFactory) {
        this.entityIdConverterFactory = entityIdConverterFactory;
        return this;
    }

    public BaseMongoStorageAccessorFactory setEntityObjectConverter(MongoEntityConverter entityObjectConverter) {
        this.entityObjectConverter = entityObjectConverter;
        return this;
    }

    public BaseMongoStorageAccessorFactory setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        return this;
    }

}
