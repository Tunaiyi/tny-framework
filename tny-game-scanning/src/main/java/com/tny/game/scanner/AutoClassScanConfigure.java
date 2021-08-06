package com.tny.game.scanner;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * <p>
 */
public final class AutoClassScanConfigure {

	private final static String FACTORY_FILE_PATH = "META-INF/tny-factory.properties";

	private static final ConcurrentMap<Class<?>, Set<Class<?>>> CLASSES_MAP = new ConcurrentHashMap<>();

	private static volatile boolean init = false;

	private AutoClassScanConfigure() {
	}

	private static void init() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Enumeration<URL> dirs = classLoader.getResources(FACTORY_FILE_PATH);
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				Properties properties = new Properties();
				URLConnection con = url.openConnection();
				try (InputStream stream = con.getInputStream()) {
					properties.load(stream);
				} catch (IOException ex) {
					if (con instanceof HttpURLConnection) {
						((HttpURLConnection)con).disconnect();
					}
					throw ex;
				}
				for (Entry<Object, Object> entry : properties.entrySet()) {
					if (entry == null) {
						continue;
					}
					String classesValue = entry.getValue().toString();
					String keyValue = entry.getKey().toString();
					String[] classes = StringUtils.split(classesValue, ",");
					Set<Class<?>> set = CLASSES_MAP.computeIfAbsent(Class.forName(keyValue), (k) -> new CopyOnWriteArraySet<>());
					Set<Class<?>> temp = new HashSet<>();
					for (String clazz : classes) {
						if (StringUtils.isNoneBlank(clazz)) {
							temp.add(Class.forName(clazz));
						}
					}
					set.addAll(temp);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static Set<Class<?>> getClasses(Class<?> clazz) {
		if (init) {
			return CLASSES_MAP.getOrDefault(clazz, Collections.emptySet());
		}
		synchronized (AutoClassScanConfigure.class) {
			if (!init) {
				AutoClassScanConfigure.init();
				init = true;
			}
		}
		return CLASSES_MAP.getOrDefault(clazz, Collections.emptySet());
	}

}
