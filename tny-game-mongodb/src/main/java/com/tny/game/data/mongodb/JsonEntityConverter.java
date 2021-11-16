package com.tny.game.data.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.codec.jackson.mapper.*;

/**
 * <p>
 */

public class JsonEntityConverter extends AbstractEntityConverter {

	private final ObjectMapper objectMapper;

	public JsonEntityConverter() {
		this(new NoopEntityOnLoadService(), ObjectMapperFactory.createMapper());
	}

	public JsonEntityConverter(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	public JsonEntityConverter(EntityOnLoadService objectOnLoadService) {
		this(objectOnLoadService, ObjectMapperFactory.defaultMapper());
	}

	public JsonEntityConverter(EntityOnLoadService objectOnLoadService, ObjectMapper objectMapper) {
		super(objectOnLoadService);
		this.objectMapper = objectMapper;
	}

	@Override
	public <T> T doConvert(Object source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}
		return objectMapper.convertValue(source, targetClass);
	}

}