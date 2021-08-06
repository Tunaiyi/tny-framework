package com.tny.game.scanner;

import org.springframework.core.io.support.*;
import org.springframework.core.type.classreading.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/2 3:28 上午
 */
public final class ClassMetadataReaderFactory {

	private static MetadataReaderFactory readerFactory;// = new CachingMetadataReaderFactory(resourcePatternResolver);

	public static void init(ClassLoader classLoader) {
		// = new PathMatchingResourcePatternResolver();
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
		readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
	}

	public static MetadataReaderFactory getFactory() {
		return readerFactory;
	}

	public static MetadataReaderFactory createReaderFactory(ClassLoader classLoader) {
		PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
		readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		return readerFactory;
	}

}
