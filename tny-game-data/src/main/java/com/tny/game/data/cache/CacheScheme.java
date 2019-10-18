package com.tny.game.data.cache;

import com.tny.game.common.reflect.*;
import com.tny.game.data.annotation.*;

import java.util.List;

/**
 * <p>
 */
public class CacheScheme {

    private Class<?> objectClass;

    private CacheId cacheId;

    private CacheObject cache;

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
        return objectClass;
    }

    public long maxCacheSize() {
        return cache.maxCacheSize();
    }

    public int concurrencyLevel() {
        return cache.concurrencyLevel();
    }

}
