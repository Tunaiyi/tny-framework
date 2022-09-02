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
package com.tny.game.data.mongodb;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public class MongoClientStorageAccessorFactory extends BaseMongoStorageAccessorFactory {

    public static final String ACCESSOR_NAME = "mongoClientStorageAccessorFactory";

    public MongoClientStorageAccessorFactory(String dataSource) {
        super(dataSource);
    }

    public MongoClientStorageAccessorFactory(EntityIdConverterFactory entityIdConverterFactory,
            MongoEntityConverter entityObjectConverter, MongoTemplate mongoTemplate, String dataSource) {
        super(entityIdConverterFactory, entityObjectConverter, mongoTemplate, dataSource);
    }

    @Override
    protected StorageAccessor<?, ?> newMongoStorageAccessor(Class<?> entityClass, EntityIdConverter<?, ?, ?> idConverter) {
        return new MongoClientStorageAccessor<>(entityClass, as(idConverter), entityObjectConverter, mongoTemplate, dataSource);
    }

}
