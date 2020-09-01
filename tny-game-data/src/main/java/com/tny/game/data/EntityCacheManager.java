package com.tny.game.data;

import com.tny.game.common.concurrent.lock.locker.*;
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
        Lock lock = this.locker.lock(id);
        O object;
        try {
            object = this.cache.get(id);
            if (object != null)
                return object;
            object = this.storage.get(id);
            if (object == null)
                object = creator.create(id);
            this.storage.insert(id, object);
            this.cache.put(id, object);
        } finally {
            this.locker.unlock(id, lock);
        }
        return object;
    }

    private K idOf(O object) {
        // if (object instanceof CacheObject)
        //     return as(as(object, CacheObject.class).getCacheId());
        return this.cache.idOf(object);
    }

    @Override
    public boolean insert(O object) {
        K id = idOf(object);
        Lock lock = this.locker.lock(id);
        try {
            O old = this.cache.get(id);
            if (old != null)
                return false;
            if (!this.storage.insert(id, object))
                return false;
            this.cache.put(id, object);
        } finally {
            this.locker.unlock(id, lock);
        }
        return true;
    }

    @Override
    public O get(K id) {
        Lock lock = this.locker.lock(id);
        O object;
        try {
            object = this.cache.get(id);
            if (object != null)
                return object;
            object = this.storage.get(id);
            if (object == null)
                return null;
            this.cache.put(id, object);
        } finally {
            this.locker.unlock(id, lock);
        }
        return object;
    }

    @Override
    public boolean update(O object) {
        return this.storage.update(idOf(object), object);
    }

    @Override
    public boolean save(O object) {
        return this.storage.save(idOf(object), object);
    }

    @Override
    public O delete(K id) {
        Lock lock = this.locker.lock(id);
        O object;
        try {
            object = this.cache.remove(id);
            if (object == null)
                return null;
            this.cache.remove(id);
            this.storage.delete(id, object);
        } finally {
            this.locker.unlock(id, lock);
        }
        return object;
    }
}
