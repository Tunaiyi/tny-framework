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
