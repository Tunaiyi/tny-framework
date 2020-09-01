package com.tny.game.data.cache;

import com.tny.game.common.reflect.*;
import com.tny.game.data.annotation.*;

import java.util.List;

/**
 * <p>
 */
public class CacheScheme {

    private final Class<?> objectClass;

    private final CacheId cacheId;

    private final CacheObject cache;

    private List<PropertyAccessor> idPropertyAccessors;

    public CacheScheme(Class<?> objectClass, CacheObject cache, CacheId cacheId) {
        this.objectClass = objectClass;
        this.cache = cache;
        this.cacheId = cacheId;
        // if (cacheId != null) {
        //     List<Field> idFieldList = ReflectAide.getDeepFieldByAnnotation(objectClass, CacheId.class);
        //     this.initIDMethods(gClass, idFieldList);
        // } else {
        //     this.initIDMethods(gClass, this.toCache.cacheKeys());
        // }
    }

    public Class<?> getObjectClass() {
        return this.objectClass;
    }

    public long maxCacheSize() {
        return this.cache.maxCacheSize();
    }

    public int concurrencyLevel() {
        return this.cache.concurrencyLevel();
    }

}
