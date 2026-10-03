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

package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.basics.item.*;
import com.tny.game.data.mongodb.*;
import org.bson.Document;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/21 4:23 下午
 */
public class GameJsonMongoEntityConverter extends JsonMongoEntityConverter {

    public GameJsonMongoEntityConverter(List<MongoDocumentEnhance<?>> enhances) {
        super(enhances);
    }

    public GameJsonMongoEntityConverter(ObjectMapper objectMapper, List<MongoDocumentEnhance<?>> enhances) {
        super(objectMapper, enhances);
    }

    @Override
    public Document convertToWrite(Object id, Object source) {
        Document document = this.format(source, Document.class);
        if (source instanceof Any) {
            Any any = (Any) source;
            document.put("_id", id);
            if (!document.containsKey("id")) {
                document.put("id", any.getId());
            }
        }
        return document;
    }

}
