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
