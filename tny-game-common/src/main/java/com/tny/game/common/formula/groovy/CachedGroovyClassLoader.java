package com.tny.game.common.formula.groovy;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedGroovyClassLoader extends GroovyClassLoader {

    private static final Map<String, Class<?>> CACHE = new ConcurrentHashMap<>();

    protected CachedGroovyClassLoader() {
        super();
    }

    protected CachedGroovyClassLoader(ClassLoader parent, CompilerConfiguration config, boolean useConfigurationClasspath) {
        super(parent, config, useConfigurationClasspath);
    }

    protected CachedGroovyClassLoader(ClassLoader loader, CompilerConfiguration config) {
        super(loader, config);
    }

    protected CachedGroovyClassLoader(ClassLoader loader) {
        super(loader);
    }

    protected CachedGroovyClassLoader(GroovyClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> parseClass(String text, String fileName) throws CompilationFailedException {
        Class<?> clazz = CACHE.get(text);
        if (clazz == null) {
            clazz = super.parseClass(text, fileName);
            CACHE.put(text, clazz);
        }
        return clazz;
    }

}
