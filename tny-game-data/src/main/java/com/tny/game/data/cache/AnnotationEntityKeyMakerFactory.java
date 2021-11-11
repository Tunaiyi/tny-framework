package com.tny.game.data.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 3:08 下午
 */
public class AnnotationEntityKeyMakerFactory implements EntityKeyMakerFactory {

	public static final String MAKER_NAME = "annotationEntityKeyMakerFactory";

	private static final Map<Class<?>, EntityKeyMaker<?, ?>> makerMap = new ConcurrentHashMap<>();

	@Override
	public EntityKeyMaker<?, ?> createMaker(EntityScheme scheme) {
		Class<?> entityClass = scheme.getEntityClass();
		return makerMap.computeIfAbsent(entityClass, AnnotationEntityKeyMaker::new);
	}

}
