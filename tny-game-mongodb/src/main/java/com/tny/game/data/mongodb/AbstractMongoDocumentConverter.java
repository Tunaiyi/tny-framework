package com.tny.game.data.mongodb;

import com.tny.game.common.runtime.*;
import com.tny.game.common.utils.*;
import org.bson.Document;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */

public abstract class AbstractMongoDocumentConverter implements MongoDocumentConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMongoDocumentConverter.class);

	private static final ProcessWatcher NET_TRACE_ALL_WATCHER = ProcessWatcher
			.of(MongoDocumentMapper.class, TrackPrintOption.CLOSE).schedule(15, TimeUnit.SECONDS);

	private final List<MongoDocumentEnhance<?>> enhances = new ArrayList<>();

	private final Map<Class<?>, MongoDocumentEnhance<?>> enhanceMap = new ConcurrentHashMap<>();

	protected AbstractMongoDocumentConverter(List<MongoDocumentEnhance<?>> enhances) {
		enhances.forEach(e -> {
			Object old = enhanceMap.put(e.type(), e);
			if (old != null) {
				throw new IllegalArgumentException(StringAide.format("{} 与 {} 相同的处理类型 {}", e, old, e.type()));
			}
		});
	}

	private <T> MongoDocumentEnhance<T> enhance(Class<?> clazz) {
		MongoDocumentEnhance<?> enhance = enhanceMap.get(clazz);
		if (enhance != null) {
			return as(enhance);
		}
		for (MongoDocumentEnhance<?> one : enhances) {
			if (one.type().isAssignableFrom(clazz)) {
				MongoDocumentEnhance<?> old = enhanceMap.putIfAbsent(one.type(), one);
				return as(old == null ? one : old);
			}
		}
		return null;
	}

	@Override
	public <T> T fromDocument(Document source, Class<T> targetClass) {
		T object = format(source, targetClass);
		MongoDocumentEnhance<T> enhance = enhance(targetClass);
		if (enhance != null) {
			enhance.onRead(source, object);
		}
		return object;
	}

	@Override
	public Document toDocument(Object source) {
		Document document = format(source, Document.class);
		MongoDocumentEnhance<Object> enhance = enhance(source.getClass());
		if (enhance != null) {
			enhance.onWrite(source, document);
		}
		return document;
	}

	protected <T> T format(Object source, Class<T> targetClass) {
		if (source == null) {
			return null;
		}
		ProcessTracer tracer = NET_TRACE_ALL_WATCHER.trace();
		T target = doFormat(source, targetClass);
		tracer.done();
		return target;
	}

	protected abstract <T> T doFormat(Object source, Class<T> targetClass);

}