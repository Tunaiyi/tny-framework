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
            Any any = (Any)source;
            document.put("_id", id);
            if (!document.containsKey("id")) {
                document.put("id", any.getId());
            }
        }
        return document;
    }

}
