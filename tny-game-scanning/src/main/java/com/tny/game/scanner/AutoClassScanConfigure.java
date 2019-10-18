package com.tny.game.scanner;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 */
public final class AutoClassScanConfigure {

    private final static String FACTORY_FILE_PATH = "META-INF/tny-factory.properties";

    private static ConcurrentMap<Class<?>, Set<Class<?>>> CLASSES_MAP = new ConcurrentHashMap<>();

    private AutoClassScanConfigure() {
    }

    static {
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
                        ((HttpURLConnection) con).disconnect();
                    }
                    throw ex;
                }
                for (Entry<Object, Object> entry : properties.entrySet()) {
                    if (entry == null)
                        continue;
                    String classesValue = entry.getValue().toString();
                    String keyValue = entry.getKey().toString();
                    String[] classes = StringUtils.split(classesValue, ",");
                    Set<Class<?>> set = CLASSES_MAP.computeIfAbsent(Class.forName(keyValue), (k) -> new CopyOnWriteArraySet<>());
                    Set<Class<?>> temp = new HashSet<>();
                    for (String clazz : classes) {
                        if (StringUtils.isNoneBlank(clazz))
                            temp.add(Class.forName(clazz));
                    }
                    set.addAll(temp);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static Set<Class<?>> getClasses(Class<?> clazz) {
        return CLASSES_MAP.getOrDefault(clazz, Collections.emptySet());
    }
}
