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
import com.tny.game.data.cache.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/12 2:59 下午
 */
public class MongoEntityIdConverterFactory implements EntityIdConverterFactory {

    private static final Map<Class<?>, MongoEntityIdConverter<?, ?>> converterMap = new ConcurrentHashMap<>();

    @Override
    public EntityIdConverter<?, ?, ?> createConverter(EntityScheme scheme, EntityKeyMaker<?, ?> keyMaker) {
        return converterMap.computeIfAbsent(scheme.getCacheClass(), MongoEntityIdConverter::new);
    }

}
