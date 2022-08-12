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

import com.tny.game.data.accessor.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/11 3:07 下午
 */
public abstract class MongoStorageAccessor<K extends Comparable<?>, O> implements BatchStorageAccessor<K, O> {

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