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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/21 9:10 下午
 */
public class JsonMongoEntityConverter extends JsonMongoDocumentConverter implements MongoEntityConverter {

    public JsonMongoEntityConverter(List<MongoDocumentEnhance<?>> enhances) {
        super(enhances);
    }

    public JsonMongoEntityConverter(ObjectMapper objectMapper, List<MongoDocumentEnhance<?>> enhances) {
        super(objectMapper, enhances);
    }

    @Override
    public <T> T convertToRead(Document source, Class<T> targetClass) {
        return format(source, targetClass);
    }

    @Override
    public Document convertToWrite(Object id, Object source) {
        return format(source, Document.class);
    }

}
