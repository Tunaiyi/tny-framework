package com.tny.game.data.mongodb;

import com.tny.game.common.runtime.*;
import org.slf4j.*;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 */

public abstract class AbstractEntityConverter implements EntityConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEntityConverter.class);

	private final EntityOnLoadService objectOnLoadService;

	private static final ProcessWatcher NET_TRACE_ALL_WATCHER = ProcessWatcher
			.of(EntityConverter.class, TrackPrintOption.CLOSE).schedule(15, TimeUnit.SECONDS);

	public AbstractEntityConverter() {
		this(new NoopEntityOnLoadService());
	}

	public AbstractEntityConverter(EntityOnLoadService objectOnLoadService) {
		this.objectOnLoadService = objectOnLoadService;
	}

	@Override
	public <T> T convert(Object source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}
		ProcessTracer tracer = NET_TRACE_ALL_WATCHER.trace();
		T target = doConvert(source, targetClass);
		tracer.done();
		return objectOnLoadService.onLoad(target);
	}

	public abstract <T> T doConvert(Object source, Class<T> targetClass);

}