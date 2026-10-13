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
package com.tny.game.data.configuration.redisson;

import com.tny.game.boot.utils.*;
import com.tny.game.data.cache.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class RedissonStorageAccessorFactorySetting {

    private String dataSource;

    private String tableHead;

    private String idConverterFactory = BeanNameUtils.lowerCamelName(CacheKeyMakerIdConverterFactory.class);

    public String getDataSource() {
        return dataSource;
    }

    public RedissonStorageAccessorFactorySetting setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public String getTableHead() {
        return tableHead;
    }

    public RedissonStorageAccessorFactorySetting setTableHead(String tableHead) {
        this.tableHead = tableHead;
        return this;
    }

    public String getIdConverterFactory() {
        return idConverterFactory;
    }

    public RedissonStorageAccessorFactorySetting setIdConverterFactory(String idConverterFactory) {
        this.idConverterFactory = idConverterFactory;
        return this;
    }

}
