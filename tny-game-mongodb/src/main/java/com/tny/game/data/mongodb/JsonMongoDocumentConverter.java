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