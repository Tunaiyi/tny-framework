package com.tny.game.cache;

public final class DefaultCacheFormatter extends CacheFormatter<Object, Object> {

    public static final CacheFormatter<?, ?> DEFAULT_HANDLER = new DefaultCacheFormatter();

    private DefaultCacheFormatter() {
    }

    @Override
    public Object format4Load(String key, Object object) {
        return object;
    }

    @Override
    public Object format2Save(String key, Object object) {
        return object;
    }

}