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
package com.tny.game.data.redisson;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;
import com.tny.game.redisson.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:21 下午
 */
public class RedissonStorageAccessorFactory implements StorageAccessorFactory {

    public static final String ACCESSOR_NAME = "redissonStorageAccessorFactory";

    private String tableHead;

    private String dataSource;

    private EntityIdConverterFactory entityIdConverterFactory;

    public RedissonStorageAccessorFactory(String dataSource, String tableHead) {
        this.tableHead = ifNull(tableHead, "");
        this.dataSource = dataSource;
    }

    @Override
    public <A extends StorageAccessor<?, ?>> A createAccessor(EntityScheme scheme, CacheKeyMaker<?, ?> keyMaker) {
        Class<?> entityClass = scheme.getEntityClass();
        TypedRedisson<?> redisson = RedissonFactory.createTypedRedisson(entityClass);
        String tableKey = StringUtils.isEmpty(tableHead) ? entityClass.getSimpleName() : tableHead + ":" + entityClass.getSimpleName();
        EntityIdConverter<?, ?, ?> idConverter = entityIdConverterFactory.createConverter(scheme, keyMaker);
        return as(new RedissonStorageAccessor<>(dataSource, tableKey, idConverter, as(redisson)));
    }

    public RedissonStorageAccessorFactory setTableHead(String tableHead) {
        this.tableHead = tableHead;
        return this;
    }

    public RedissonStorageAccessorFactory setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public RedissonStorageAccessorFactory setEntityIdConverterFactory(EntityIdConverterFactory entityIdConverterFactory) {
        this.entityIdConverterFactory = entityIdConverterFactory;
        return this;
    }

}
