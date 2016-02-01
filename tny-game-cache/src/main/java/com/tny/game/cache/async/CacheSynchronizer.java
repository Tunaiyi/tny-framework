package com.tny.game.cache.async;

import com.tny.game.asyndb.Synchronizer;
import com.tny.game.cache.DirectCache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CacheSynchronizer<T> implements Synchronizer<T> {

    //	private final static Logger logger = LoggerFactory.getLogger(LogName.CACHE_SYNCHRONIZER);

    private DirectCache cameCache;

    private boolean toDB = true;

    public CacheSynchronizer(DirectCache cameCache) {
        super();
        this.cameCache = cameCache;
    }

    @Override
    public T get(Class<? extends T> clazz, String key) {
        return this.cameCache.getObjectByKey(clazz, key);
    }

    @Override
    public Map<String, ? extends T> get(Class<? extends T> clazz, Collection<String> keyValues) {
        return this.cameCache.getObjectMapByKeys(clazz, keyValues);
    }

    //	@Override
    //	public T get_(Class<? extends T> clazz, String key) {
    //		try {
    //			return this.cameCache.getObject(clazz, key);
    //		} catch (Exception e) {
    //			logger.error("", e);
    //			return null;
    //		}
    //	}

    @Override
    public boolean insert(T object) {
        if (!this.toDB)
            return true;
        return this.cameCache.addObject(object);
    }

    @Override
    public boolean update(T object) {
        if (!this.toDB)
            return true;
        return this.cameCache.updateObject(object);
    }

    @Override
    public boolean delete(T object) {
        if (!this.toDB)
            return true;
        return this.cameCache.deleteObject(object);
    }

    @Override
    public boolean save(T object) {
        if (!this.toDB)
            return true;
        return this.cameCache.setObject(object);
    }

    public void setToDB(boolean toDB) {
        this.toDB = toDB;
    }

    @Override
    public Collection<T> insert(Collection<T> objects) {
        if (!this.toDB)
            return Collections.emptyList();
        return this.cameCache.addObject(objects);
    }

    @Override
    public Collection<T> update(Collection<T> objects) {
        if (!this.toDB)
            return Collections.emptyList();
        return this.cameCache.updateObject(objects);
    }

    @Override
    public Collection<T> delete(Collection<T> objects) {
        if (!this.toDB)
            return Collections.emptyList();
        return this.cameCache.deleteObject(objects);
    }

    @Override
    public Collection<T> save(Collection<T> objects) {
        if (!this.toDB)
            return Collections.emptyList();
        return this.cameCache.setObject(objects);
    }

}
