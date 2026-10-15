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

import com.tny.game.data.accessor.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:07 下午
 */
public abstract class MongoStorageAccessor<K extends Comparable<?>, O> implements AsyncStorageAccessor<K, O> {

    private final String dataSource;

    protected final Class<O> entityClass;

    public MongoStorageAccessor(Class<O> entityClass, String dataSource) {
        this.entityClass = entityClass;
        this.dataSource = dataSource;
    }

    public Class<O> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

}