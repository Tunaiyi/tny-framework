package com.tny.game.data.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tny.game.codec.jackson.mapper.*;

/**
 * <p>
 */

public class JsonEntityDocumentConverter extends AbstractEntityDocumentConverter {

	private final ObjectMapper objectMapper;

	public JsonEntityDocumentConverter() {
		this(new NoopEntityOnLoadService(), ObjectMapperFactory.createMapper());
	}

	public JsonEntityDocumentConverter(ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	public JsonEntityDocumentConverter(EntityOnLoadService objectOnLoadService) {
		this(objectOnLoadService, ObjectMapperFactory.defaultMapper());
	}

	public JsonEntityDocumentConverter(EntityOnLoadService objectOnLoadService, ObjectMapper objectMapper) {
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