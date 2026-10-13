/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.cache.async;

import com.tny.game.asyndb.*;
import com.tny.game.cache.*;
import org.slf4j.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class AsyncCache implements Cache {

    public static final Logger LOGGER = LoggerFactory.getLogger(AsyncCache.class);

    /**
     * 关联item类信息缓存
     */
    private ToCacheClassHolderFactory toCacheClassHolderFactory;

    /**
     * 对象池
     */
    private ObjectPool pool;

    public AsyncCache(ToCacheClassHolderFactory toCacheClassHolderFactory, ObjectPool pool) {
        super();
        this.toCacheClassHolderFactory = toCacheClassHolderFactory;
        this.pool = pool;
    }

    @Override
    public <T> T getObject(Class<T> clazz, Object... keyValues) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(clazz);
        String key = holder.getKey(keyValues);
        return this.pool.get(clazz, key);
    }

    @Override
    public String getKey(Object object) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(object.getClass());
        return holder.getKey(object);
    }

    @Override
    public String getKey(Class<?> clazz, Object... keyValues) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(clazz);
        return holder.getKey(keyValues);
    }

    @Override
    public <T> T getObjectByKey(Class<T> clazz, String key) {
        return this.pool.get(clazz, key);
    }

    @Override
    public <T> Collection<? extends T> getObjectsByKeys(Class<T> clazz, Collection<String> keys) {
        return this.pool.gets(clazz, keys);
    }

    @Override
    public <T> Map<String, ? extends T> getObjectMapByKeys(Class<T> clazz, Collection<String> keys) {
        return this.pool.getsMap(clazz, keys);
    }

    @Override
    public boolean addObject(Object object) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(object.getClass());
        String key = holder.getKey(object);
        return this.pool.insert(key, object);
    }

    @Override
    public <T> List<T> addObject(Collection<T> objectCollection) {
        List<T> itemList = new ArrayList<T>();
        for (T object : objectCollection) {
            if (!this.addObject(object)) {
                itemList.add(object);
            }
        }
        return itemList;
    }

    @Override
    public boolean setObject(Object object) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(object.getClass());
        String key = holder.getKey(object);
        return this.pool.save(key, object);
    }

    @Override
    public <T> List<T> setObject(Collection<T> objectCollection) {
        List<T> itemList = new ArrayList<T>();
        for (T object : objectCollection) {
            if (!this.setObject(object)) {
                itemList.add(object);
            }
        }
        return itemList;
    }

    @Override
    public boolean updateObject(Object object) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(object.getClass());
        String key = holder.getKey(object);
        return this.pool.update(key, object);
    }

    @Override
    public <T> List<T> updateObject(Collection<T> objectCollection) {
        List<T> itemList = new ArrayList<T>();
        for (T object : objectCollection) {
            if (!this.updateObject(object)) {
                itemList.add(object);
            }
        }
        return itemList;
    }

    @Override
    public boolean deleteObject(Object object) {
        ToCacheClassHolder holder = this.toCacheClassHolderFactory.getCacheClassHolder(object.getClass());
        String key = holder.getKey(object);
        return this.pool.delete(key, object);
    }

    @Override
    public <T> List<T> deleteObject(T... objects) {
        List<T> list = new ArrayList<T>();
        for (T object : objects) {
            if (!this.deleteObject(object)) {
                list.add(object);
            }
        }
        return list;
    }

    @Override
    public <T> List<T> deleteObject(Collection<T> objectCollection) {
        List<T> list = new ArrayList<T>();
        for (T object : objectCollection) {
            if (!this.deleteObject(object)) {
                list.add(object);
            }
        }
        return list;
    }

    public void flushAll() {
        this.pool.flushAll();
    }

    // ##########################################################################################
    // ##########################################################################################
    // --------------------------------------- 不支持以下操作
    // --------------------------------------

    @Override
    public boolean addObject(Object value, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> addObject(Collection<T> objectCollection, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setObject(Object value, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> setObject(Collection<T> objectCollection, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updateObject(Object value, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> updateObject(Collection<T> objectCollection, long millisecond) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> CasItem<T> getsObject(Class<T> clazz, String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> CasItem<T> getsObject(Class<T> clazz, Object... keyValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> boolean casObject(CasItem<T> item) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> boolean casObject(CasItem<T> item, long millisecond) {
        throw new UnsupportedOperationException();
    }

    // ##########################################################################################
    // ##########################################################################################

    @Override
    public void shutdown() throws InterruptedException {
        this.pool.shutdown();
    }

}
