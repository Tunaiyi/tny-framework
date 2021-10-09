package com.tny.game.data;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.data.annotation.*;
import com.tny.game.data.cache.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import com.tny.game.scanner.filter.*;
import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class DataClassLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataClassLoader.class);

	private static final Set<Class<?>> cacheObjectClasses = new ConcurrentHashSet<>();

	private static final Set<CacheScheme> cacheSchemeSchemes = new ConcurrentHashSet<>();

	@ClassSelectorProvider
	static ClassSelector cacheObjectSelector() {
		return ClassSelector.create()
				.addFilter(AnnotationClassFilter.ofInclude(EntityObject.class))
				.setHandler((classes) -> {
					cacheObjectClasses.addAll(classes);
					registerScheme(classes);
					LOGGER.info("DataClassLoader.CacheObject : {}", cacheObjectClasses.size());
				});
	}

	public static Set<Class<?>> getAllCacheObjectClasses() {
		return Collections.unmodifiableSet(cacheObjectClasses);
	}

	public static Set<CacheScheme> getAllCacheSchemeSchemes() {
		return Collections.unmodifiableSet(cacheSchemeSchemes);
	}

	private static void registerScheme(Collection<Class<?>> classes) {
		cacheObjectClasses.addAll(classes);
		Map<Class<?>, CacheScheme> schemeMap = new HashMap<>();
		for (Class<?> clazz : classes) {
			parseScheme(clazz, schemeMap);
		}
		cacheSchemeSchemes.addAll(schemeMap.values());
	}

	private static void parseScheme(Class<?> clazz, Map<Class<?>, CacheScheme> schemeMap) {
		if (schemeMap.containsKey(clazz)) {
			return;
		}
		CacheScheme scheme = new CacheScheme(clazz);
		if (scheme.isCacheSelf()) {
			schemeMap.put(scheme.getEntityClass(), scheme);
		} else {
			parseScheme(scheme.getCacheClass(), schemeMap);
		}
	}

}
