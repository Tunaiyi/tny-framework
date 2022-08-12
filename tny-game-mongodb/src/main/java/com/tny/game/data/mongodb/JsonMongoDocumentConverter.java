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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.codec.jackson.mapper.*;

import java.util.List;

/**
 * <p>
 */

public class JsonMongoDocumentConverter extends AbstractMongoDocumentConverter {

    private final ObjectMapper objectMapper;

    public JsonMongoDocumentConverter(List<MongoDocumentEnhance<?>> enhances) {
        this(ObjectMapperFactory.createMapper(), enhances);
    }

    public JsonMongoDocumentConverter(ObjectMapper objectMapper, List<MongoDocumentEnhance<?>> enhances) {
        super(enhances);
        this.objectMapper = objectMapper;
    }

    @Override
    protected <T> T doFormat(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return objectMapper.convertValue(source, targetClass);
    }

}