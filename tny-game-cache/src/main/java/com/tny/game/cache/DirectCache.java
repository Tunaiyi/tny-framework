package com.tny.game.cache;

import com.tny.game.cache.simple.SimpleCacheItemFactory;
import com.tny.game.cache.simple.SimpleCasItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author KGTny
 *         <p>
 *         缓存抽象实体
 *         <p>
 *         提供缓存锁的实现<br>
 */
public class DirectCache implements Cache {

    /**
     * 关联item类信息缓存
     */
    private ToCacheClassHolderFactory toCCHolderFactory;

    /**
     * 缓存客户端
     */
    protected CacheClient client;

    private RawCacheItemFactory cacheItemFactory;

    public DirectCache(CacheClient client, ToCacheClassHolderFactory toCCHolderFactory) {
        this(client, new SimpleCacheItemFactory(), toCCHolderFactory);
    }

    public DirectCache(CacheClient client, RawCacheItemFactory cacheItemFactory, ToCacheClassHolderFactory toCCHolderFactory) {
        this.client = client;
        this.cacheItemFactory = cacheItemFactory;
        this.toCCHolderFactory = toCCHolderFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CasItem<T> getsObject(Class<T> clazz, Object... keyValues) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(clazz);
        String key = holder.getKey(keyValues);
        final CasItem<?> mItem = this.client.gets(key);
        if (mItem == null)
            return null;
        Object object = mItem.getData();
        if (object == null)
            return null;
        Object loadObject = holder.triggerLoad(key, object);
        if (loadObject == null)
            return null;
        if (!clazz.isInstance(loadObject))
            throw new ClassCastException("memcached get " + key + " - " + object + " is " + object.getClass() + ", not " + clazz);
        return new SimpleCasItem<>(mItem, (T) loadObject);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CasItem<T> getsObject(Class<T> clazz, String key) {
        final CasItem<?> mItem = this.client.gets(key);
        if (mItem == null)
            return null;
        Object object = mItem.getData();
        if (object == null)
            return null;
        TriggerHolder triggerHolder = this.toCCHolderFactory.getTriggerHolder(clazz);
        Object loadObject = triggerHolder.triggerLoad(key, object);
        if (loadObject == null)
            return null;
        if (!clazz.isInstance(loadObject))
            throw new ClassCastException("memcached get " + key + " - " + object + " is " + object.getClass() + ", not " + clazz);
        return new SimpleCasItem<>(mItem, (T) loadObject);
    }

    @Override
    public <T> boolean casObject(CasItem<T> item) {
        return this.casObject(item, 0L);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> boolean casObject(CasItem<T> item, long millisecond) {
        T object = (T) this.getObjectByKey(item.getData().getClass(), item.getKey());
        if (object == null)
            return false;
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(item.getData().getClass());
        String key = holder.getKey(item.getData());
        Object saveObject = holder.triggerSave(key, item.getData());
        CasItem<Object> newItem = new SimpleCasItem<>(item, saveObject);
        return this.client.cas(newItem, millisecond);
    }

    //	public void flushAll() {
    //		this.client.flushAll();
    //	}

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObjectByKey(Class<T> clazz, String key) {
        Object object = this.client.get(key);
        if (object == null)
            return null;
        TriggerHolder triggerHolder = this.toCCHolderFactory.getTriggerHolder(clazz);
        Object loadObject = triggerHolder.triggerLoad(key, object);
        if (loadObject == null)
            return null;
        if (!clazz.isInstance(loadObject))
            throw new ClassCastException("memcached get " + key + " - " + object + " is " + object.getClass() + ", not " + clazz);
        return (T) loadObject;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getObjectsByKeys(Class<T> clazz, Collection<String> keys) {
        Collection<T> returnCollection = new ArrayList<>(keys.size());
        Map<String, Object> collectionMap = this.client.getMultiMap(keys);
        if (collectionMap == null)
            return Collections.emptyList();
        for (Entry<String, Object> entity : collectionMap.entrySet()) {
            String key = entity.getKey();
            Object object = entity.getValue();
            if (object == null)
                continue;
            ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(clazz);
            Object loadObject = holder.triggerLoad(key, object);
            if (loadObject == null)
                continue;
            if (!clazz.isInstance(loadObject))
                throw new ClassCastException("memcached get " + entity.getKey() + " - " + object + " is " + object.getClass() + ", not " + clazz);
            returnCollection.add((T) loadObject);
        }
        return returnCollection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getObjectMapByKeys(Class<T> clazz, Collection<String> keys) {
        Map<String, T> returnMap = new HashMap<>(keys.size());
        Map<String, Object> collectionMap = this.client.getMultiMap(keys);
        if (collectionMap == null)
            return Collections.emptyMap();
        for (Entry<String, Object> entity : collectionMap.entrySet()) {
            String key = entity.getKey();
            Object object = entity.getValue();
            if (object == null)
                continue;
            ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(clazz);
            Object loadObject = holder.triggerLoad(key, object);
            if (loadObject == null)
                continue;
            if (!clazz.isInstance(loadObject))
                throw new ClassCastException("memcached get " + entity.getKey() + " - " + object + " is " + object.getClass() + ", not " + clazz);
            returnMap.put(key, (T) loadObject);
        }
        return returnMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getObject(Class<T> clazz, Object... keyValues) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(clazz);
        String key = holder.getKey(keyValues);
        Object object = this.client.get(key);
        if (object == null)
            return null;
        TriggerHolder triggerHolder = this.toCCHolderFactory.getTriggerHolder(clazz);
        Object loadObject = triggerHolder.triggerLoad(key, object);
        if (loadObject == null)
            return null;
        if (!clazz.isInstance(loadObject))
            throw new ClassCastException("memcached get " + key + " - " + object + " is " + object.getClass() + ", not " + clazz);
        return (T) loadObject;
    }

    @Override
    public boolean deleteObject(Object object) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(object.getClass());
        String key = holder.getKey(object);
        holder.triggerDelete(key, object);
        return this.client.delete(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> deleteObject(T... objects) {
        List<T> list = new ArrayList<>();
        for (T object : objects) {
            if (!this.deleteObject(object))
                list.add(object);
        }
        return list;
    }

    @Override
    public <T> List<T> deleteObject(Collection<T> objectCollection) {
        List<T> list = new ArrayList<>();
        for (T object : objectCollection) {
            if (!this.deleteObject(object))
                list.add(object);
        }
        return list;
    }

    @Override
    public boolean addObject(Object value) {
        return this.addObject(value, 0L);
    }

    @Override
    public boolean addObject(Object value, long millisecond) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(value.getClass());
        String key = holder.getKey(value);
        Object saveObject = holder.triggerInsert(key, value);
        return this.client.add(key, saveObject, millisecond);
    }

    @Override
    public <T> List<T> addObject(Collection<T> objectCollection) {
        return this.addObject(objectCollection, 0);
    }

    @Override
    public <T> List<T> addObject(Collection<T> objectCollection, long millisecond) {
        if (objectCollection.isEmpty())
            return Collections.emptyList();
        List<RawCacheItem<T, ?>> items = CacheItemHelper.objects2CacheItems(
                this.toCCHolderFactory, this.cacheItemFactory,
                objectCollection, CacheItemHelper.OPERATION_ADD, millisecond);
        List<RawCacheItem<T, ?>> failed = this.client.addMultis(items);
        return CacheItemHelper.cacheItem2Object(failed);
    }

    @Override
    public boolean setObject(Object value) {
        return this.setObject(value, 0L);
    }

    @Override
    public boolean setObject(Object value, long millisecond) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(value.getClass());
        String key = holder.getKey(value);
        Object saveObject = holder.triggerSave(key, value);
        return this.client.set(key, saveObject, millisecond);
    }

    @Override
    public <T> List<T> setObject(Collection<T> objectCollection) {
        return this.setObject(objectCollection, 0L);
    }

    @Override
    public <T> List<T> setObject(Collection<T> objectCollection, long millisecond) {
        if (objectCollection.isEmpty())
            return Collections.emptyList();
        List<RawCacheItem<T, ?>> items = CacheItemHelper.objects2CacheItems(
                this.toCCHolderFactory, this.cacheItemFactory,
                objectCollection, CacheItemHelper.OPERATION_ADD, millisecond);
        List<RawCacheItem<T, ?>> failed = this.client.setMultis(items);
        return CacheItemHelper.cacheItem2Object(failed);
    }

    @Override
    public boolean updateObject(Object value) {
        return this.updateObject(value, 0L);
    }

    @Override
    public boolean updateObject(Object value, long millisecond) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(value.getClass());
        String key = holder.getKey(value);
        Object saveObject = holder.triggerUpdate(key, value);
        return this.client.update(key, saveObject, millisecond);
    }

    @Override
    public <T> List<T> updateObject(Collection<T> objectCollection) {
        return this.updateObject(objectCollection, 0L);
    }

    @Override
    public <T> List<T> updateObject(Collection<T> objectCollection, long millisecond) {
        if (objectCollection.isEmpty())
            return Collections.emptyList();
        List<RawCacheItem<T, ?>> items = CacheItemHelper.objects2CacheItems(
                this.toCCHolderFactory, this.cacheItemFactory,
                objectCollection, CacheItemHelper.OPERATION_ADD, millisecond);
        List<RawCacheItem<T, ?>> failed = this.client.updateMultis(items);
        return CacheItemHelper.cacheItem2Object(failed);
    }

    @Override
    public void shutdown() {
        this.client.shutdown();
    }

    @Override
    public String getKey(Object object) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(object.getClass());
        return holder.getKey(object);
    }

    @Override
    public String getKey(Class<?> clazz, Object... keyValues) {
        ToCacheClassHolder holder = this.toCCHolderFactory.getCacheClassHolder(clazz);
        return holder.getKey(keyValues);
    }

}
