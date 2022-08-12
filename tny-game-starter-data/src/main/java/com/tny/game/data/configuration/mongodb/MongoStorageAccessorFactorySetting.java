/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.configuration.mongodb;

import com.tny.game.boot.utils.*;
import com.tny.game.data.mongodb.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/29 4:59 下午
 */
public class MongoStorageAccessorFactorySetting {

    private String idConverterFactory = BeanNameUtils.lowerCamelName(MongoEntityIdConverterFactory.class);

    private String entityObjectConverter = BeanNameUtils.lowerCamelName(JsonMongoEntityConverter.class);

    private String dataSource = "";

    public String getIdConverterFactory() {
        return idConverterFactory;
    }

    public MongoStorageAccessorFactorySetting setIdConverterFactory(String idConverterFactory) {
        this.idConverterFactory = idConverterFactory;
        return this;
    }

    public String getEntityObjectConverter() {
        return entityObjectConverter;
    }

    public MongoStorageAccessorFactorySetting setEntityObjectConverter(String entityObjectConverter) {
        this.entityObjectConverter = entityObjectConverter;
        return this;
    }

    public String getDataSource() {
        return dataSource;
    }

    public MongoStorageAccessorFactorySetting setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

}
