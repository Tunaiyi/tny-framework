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
