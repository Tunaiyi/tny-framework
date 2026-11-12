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
