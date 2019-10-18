package com.tny.game.data;

import com.tny.game.common.lock.locker.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.storage.*;

import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class EntityCacheManager<K extends Comparable<K>, O> implements EntityManager<K, O> {

    private ObjectCache<K, O> cache;

    private ObjectStorage<K, O> storage;

    private ObjectLocker<K> locker;

    @Override
    public O load(K id, EntityCreator<K, O> creator) {
        Lock lock = locker.lock(id);
        O object;
        try {
            object = cache.get(id);
            if (object != null)
                return object;
            object = storage.get(id);
            if (object == null)
                object = creator.create(id);
            storage.insert(id, object);
            cache.put(id, object);
        } finally {
            locker.unlock(id, lock);
        }
        return object;
    }

    private K idOf(O object) {
        // if (object instanceof CacheObject)
        //     return as(as(object, CacheObject.class).getCacheId());
        return cache.idOf(object);
    }

    @Override
    public boolean insert(O object) {
        K id = idOf(object);
        Lock lock = locker.lock(id);
        try {
            O old = cache.get(id);
            if (old != null)
                return false;
            if (!storage.insert(id, object))
                return false;
            cache.put(id, object);
        } finally {
            locker.unlock(id, lock);
        }
        return true;
    }

    @Override
    public O get(K id) {
        Lock lock = locker.lock(id);
        O object;
        try {
            object = cache.get(id);
            if (object != null)
                return object;
            object = storage.get(id);
            if (object == null)
                return null;
            cache.put(id, object);
        } finally {
            locker.unlock(id, lock);
        }
        return object;
    }

    @Override
    public boolean update(O object) {
        return storage.update(idOf(object), object);
    }

    @Override
    public boolean save(O object) {
        return storage.save(idOf(object), object);
    }

    @Override
    public O delete(K id) {
        Lock lock = locker.lock(id);
        O object;
        try {
            object = cache.remove(id);
            if (object == null)
                return null;
            cache.remove(id);
            storage.delete(id, object);
        } finally {
            locker.unlock(id, lock);
        }
        return object;
    }
}
