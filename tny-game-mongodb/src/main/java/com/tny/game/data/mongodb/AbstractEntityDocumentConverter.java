package com.tny.game.data.mongodb;

import org.slf4j.*;

/**
 * <p>
 */

public abstract class AbstractEntityDocumentConverter implements EntityConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityDocumentConverter.class);

	private final EntityOnLoadService objectOnLoadService;

	public AbstractEntityDocumentConverter() {
		this(new NoopEntityOnLoadService());
	}

	public AbstractEntityDocumentConverter(EntityOnLoadService objectOnLoadService) {
		this.objectOnLoadService = objectOnLoadService;
	}

	@Override
	public <T> T convert(Object source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}
		T target = doConvert(source, targetClass);
		return objectOnLoadService.onLoad(target);
	}

	public abstract <T> T doConvert(Object source, Class<T> targetClass);

}