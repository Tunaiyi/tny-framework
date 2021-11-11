package com.tny.game.data.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/21 9:10 下午
 */
public class JsonEntityObjectConverter extends JsonEntityConverter implements EntityObjectConverter {

	public JsonEntityObjectConverter() {
	}

	public JsonEntityObjectConverter(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	public JsonEntityObjectConverter(EntityOnLoadService objectOnLoadService) {
		super(objectOnLoadService);
	}

	public JsonEntityObjectConverter(EntityOnLoadService objectOnLoadService, ObjectMapper objectMapper) {
		super(objectOnLoadService, objectMapper);
	}

	@Override
	public <T> T convertToRead(Object source, Class<T> targetClass) {
		return convert(source, targetClass);
	}

	@Override
	public <T> T convertToWrite(Object id, Object source, Class<T> targetClass) {
		return convert(source, targetClass);
	}

}
