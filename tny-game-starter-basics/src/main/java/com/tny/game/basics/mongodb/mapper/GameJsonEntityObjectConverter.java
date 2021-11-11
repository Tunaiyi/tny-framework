package com.tny.game.basics.mongodb.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.basics.item.*;
import com.tny.game.data.mongodb.*;
import org.bson.Document;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/21 4:23 下午
 */
public class GameJsonEntityObjectConverter extends JsonEntityObjectConverter {

	public GameJsonEntityObjectConverter(EntityOnLoadService objectOnLoadService, ObjectMapper objectMapper) {
		super(objectOnLoadService, objectMapper);
	}

	@Override
	public <T> T convertToWrite(Object id, Object source, Class<T> targetClass) {
		T value = this.convert(source, targetClass);
		if (targetClass == Document.class) {
			Document document = (Document)value;
			if (source instanceof Any) {
				document.put("_id", id);
			}
		}
		return value;
	}

}
