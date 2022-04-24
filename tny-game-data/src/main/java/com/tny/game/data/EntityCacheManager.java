package com.tny.game.data;

import com.tny.game.common.concurrent.lock.locker.*;
import com.tny.game.data.cache.*;
import com.tny.game.data.exception.*;
import com.tny.game.data.storage.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class EntityCacheManager<K extends Comparable<?>, O> implements EntityManager<K, O> {

    public static final Logger LOGGER = LoggerFactory.getLogger(EntityCacheManager.class);

    /**
     * 缓存
     */
    private ObjectCache<K, O> cache;

    /**
     * id 构建器
     */
    private EntityKeyMaker<K, O> keyMaker;

    /**
     * 持久化
     */
    private ObjectStorage<K, O> storage;

    /**
     * 对象锁管理器
     */
    private ObjectLocker<K> locker;

    public EntityCacheManager() {
    }

    public EntityCacheManager(EntityKeyMaker<K, O> keyMaker, ObjectCache<K, O> cache, ObjectStorage<K, O> storage, int currentLevel) {
        this(keyMaker, cache, storage, new HashObjectLocker<>(currentLevel));
    }

    public EntityCacheManager(EntityKeyMaker<K, O> keyMaker, ObjectCache<K, O> cache, ObjectStorage<K, O> storage, ObjectLocker<K> locker) {
        this.keyMaker = keyMaker;
        this.cache = cache;
        this.storage = storage;
        this.locker = locker;
    }

    public ObjectCache<K, O> getCache() {
        return cache;
    }

    public ObjectStorage<K, O> getStorage() {
        return storage;
    }

    public String getDataSource() {
        return storage.getDataSource();
    }

    private K idOf(O object) {
        return this.keyMaker.make(object);
    }

    @Override
    public List<O> find(Map<String, Object> query, EntityOnLoad<K, O> onLoad) {
        List<O> entities = new ArrayList<>();
        List<O> findList = storage.find(query);
        for (O entity : findList) {
            O value = addCache(entity, onLoad);
            if (value != null) {
                entities.add(value);
            }
        }
        return entities;
    }

    @Override
    public List<O> findAll(EntityOnLoad<K, O> onLoad) {
        List<O> entities = new ArrayList<>();
        List<O> findList = storage.findAll();
        for (O entity : findList) {
            O value = addCache(entity, onLoad);
            if (value != null) {
                entities.add(value);
            }
        }
        return entities;
    }

    private O addCache(O entity, EntityOnLoad<K, O> onLoad) {
        K id = idOf(entity);
        Lock lock = this.locker.lock(id);
        lock.unlock();
        try {
            O object = this.cache.get(idOf(entity));
            if (object != null) {
                return object;
            } else {
                if (onLoad != null) {
                    onLoad.onLoad(null, entity);
                }
                this.cache.put(id, entity);
                return entity;
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
            throw new EntityCacheException("load exception", e);
        }
    }

    @Override
    public O loadEntity(K id, EntityCreator<K, O> creator, EntityOnLoad<K, O> onLoad) {
        Lock lock = this.locker.lock(id);
        O object;
        try {
            object = this.cache.get(id);
            if (object != null) {
                return object;
            }
            object = this.storage.get(id);
            if (object == null) {
                object = creator.create(id);
            }
            if (object == null) {
                return null;
            }
            this.storage.insert(id, object);
            if (onLoad != null) {
                onLoad.onLoad(id, object);
            }
            this.cache.put(id, object);
            return object;
        } catch (Throwable e) {
            LOGGER.error("", e);
            throw new EntityCacheException("load exception", e);
        } finally {
            this.locker.unlock(id, lock);
        }

    }

    @Override
    public O getEntity(K id, EntityOnLoad<K, O> onLoad) {
        Lock lock = this.locker.lock(id);
        O object;
        try {
            object = this.cache.get(id);
            if (object != null) {
                return object;
            }
            object = this.storage.get(id);
            if (object == null) {
                return null;
            }
            if (onLoad != null) {
                onLoad.onLoad(id, object);
            }
            this.cache.put(id, object);
        } catch (Throwable e) {
            LOGGER.error("", e);
            throw new EntityCacheException("load exception", e);
        } finally {
            this.locker.unlock(id, lock);
        }
        return object;
    }

    @Override
    public List<O> getEntities(List<K> idList, EntityOnLoad<K, O> onLoad) {
        List<O> entities = new ArrayList<>();
        List<K> missing = new ArrayList<>();
        for (K id : idList) {
            O object = this.cache.get(id);
            if (object != null) {
                entities.add(object);
            } else {
                missing.add(id);
            }
        }
        List<O> findList = this.storage.get(missing);
        for (O object : findList) {
            K id = idOf(object);
            Lock lock = this.locker.lock(id);
            try {
                O exist = this.cache.get(id);
                if (exist != null) {
                    entities.add(exist);
                } else {
                    if (onLoad != null) {
                        onLoad.onLoad(id, object);
                    }
                    this.cache.put(id, object);
                }
            } catch (Throwable e) {
                LOGGER.error("", e);
                throw new EntityCacheException("load exception", e);
            } finally {
                this.locker.unlock(id, lock);
            }
        }
        return entities;
    }

    @Override
    public boolean insertEntity(O object) {
        K id = idOf(object);
        Lock lock = this.locker.lock(id);
        try {
            O old = this.cache.get(id);
            if (old != null) {
                return false;
            }
            if (!this.storage.insert(id, object)) {
                return false;
            }
            this.cache.put(id, object);
        } catch (Throwable e) {
            LOGGER.error("", e);
        } finally {
            this.locker.unlock(id, lock);
        }
        return true;
    }

    @Override
    public boolean updateEntity(O object) {
        K id = idOf(object);
        Lock lock = this.locker.lock(id);
        try {
            O old = this.cache.get(id);
            if (old == null || old != object) {
                return false;
            }
            if (!this.storage.update(id, object)) {
                return false;
            }
            this.cache.put(id, object);
        } catch (Throwable e) {
            LOGGER.error("", e);
        } finally {
            this.locker.unlock(id, lock);
        }
        return true;
    }

    @Override
    public boolean saveEntity(O object) {
        K id = idOf(object);
        Lock lock = this.locker.lock(id);
        try {
            if (!this.storage.save(id, object)) {
                return false;
            }
            this.cache.put(id, object);
        } catch (Throwable e) {
            LOGGER.error("", e);
        } finally {
            this.locker.unlock(id, lock);
        }
        return true;
    }

    @Override
    public boolean deleteEntity(O object) {
        K id = idOf(object);
        Lock lock = this.locker.lock(id);
        try {
            if (this.cache.remove(id, object)) {
                this.storage.delete(id, object);
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        } finally {
            this.locker.unlock(id, lock);
        }
        return true;
    }

    public EntityCacheManager<K, O> setCache(ObjectCache<K, O> cache) {
        this.cache = cache;
        return this;
    }

    public EntityCacheManager<K, O> setStorage(ObjectStorage<K, O> storage) {
        this.storage = storage;
        return this;
    }

    public EntityCacheManager<K, O> setKeyMaker(EntityKeyMaker<K, O> keyMaker) {
        this.keyMaker = keyMaker;
        return this;
    }

    public EntityCacheManager<K, O> setCurrentLevel(int currentLevel) {
        this.locker = new HashObjectLocker<>(currentLevel);
        return this;
    }

}
