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
package com.tny.game.data.redisson;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.redisson.*;
import org.redisson.api.*;
import org.slf4j.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:15 下午
 */
public class RedissonStorageAccessor<K extends Comparable<?>, O> implements StorageAccessor<K, O> {

    public static final Logger LOGGER = LoggerFactory.getLogger(RedissonStorageAccessor.class);

    private final String dataSource;

    private final String table;

    private final TypedRedisson<O> redisson;

    private final EntityIdConverter<K, O, ?> idConvertor;

    public RedissonStorageAccessor(String dataSource, String table, EntityIdConverter<K, O, ?> idConvertor, TypedRedisson<O> redisson) {
        this.dataSource = dataSource;
        this.table = table;
        this.redisson = redisson;
        this.idConvertor = idConvertor;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    @Override
    public O get(K key) {
        return redisson.getMap(table).get(keyToId(key));
    }

    @Override
    public <T> List<T> find(Map<String, Object> findValue, Class<T> returnClass) {
        return new ArrayList<>();
    }

    @Override
    public <T> List<T> findAll(Class<T> returnClass) {
        return new ArrayList<>();
    }

    @Override
    public List<O> find(Map<String, Object> findValue) {
        return new ArrayList<>();
    }

    @Override
    public List<O> findAll() {
        return new ArrayList<>();
    }

    @Override
    public List<O> get(Collection<? extends K> keys) {
        return new ArrayList<>(redisson.getMap(table).getAll(keysToIds(keys, new HashSet<>())).values());
    }

    @Override
    public boolean insert(O object) {
        return redisson.getMap(table).fastPutIfAbsent(entityToId(object), object);
    }

    @Override
    public Collection<O> insert(Collection<O> objects) {
        Collection<O> failureList = new ArrayList<>();
        RMapAsync<Object, O> mapAsync = redisson.getMap(table);
        Map<RFuture<Boolean>, O> futures = new HashMap<>();
        for (O object : objects) {
            futures.put(mapAsync.fastPutIfAbsentAsync(entityToId(object), object), object);
        }
        for (Entry<RFuture<Boolean>, O> entry : futures.entrySet()) {
            RFuture<Boolean> future = entry.getKey();
            boolean failed = false;
            boolean timeot = false;
            Boolean result = null;
            try {
                result = future.get(3000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                timeot = true;
            } catch (Exception e) {
                failed = true;
                LOGGER.error("insert objects exception", e);
            }
            if (timeot || failed || result != null && !result) {
                failureList.add(entry.getValue());
            }
        }
        return failureList;
    }

    @Override
    public boolean update(O object) {
        return redisson.getMap(table).fastPutIfExists(entityToId(object), object);
    }

    @Override
    public boolean save(O object) {
        return redisson.getMap(table).fastPut(entityToId(object), object);
    }

    @Override
    public Collection<O> save(Collection<O> objects) {
        Collection<O> failureList = new ArrayList<>();
        RMapAsync<Object, O> mapAsync = redisson.getMap(table);
        Map<RFuture<Boolean>, O> futures = new HashMap<>();
        for (O object : objects) {
            futures.put(mapAsync.fastPutAsync(entityToId(object), object), object);
        }
        for (Entry<RFuture<Boolean>, O> entry : futures.entrySet()) {
            RFuture<Boolean> future = entry.getKey();
            boolean failed = false;
            boolean timeot = false;
            Boolean result = null;
            try {
                result = future.get(3000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                timeot = true;
            } catch (Exception e) {
                failed = true;
                LOGGER.error("save objects exception", e);
            }
            if (timeot || failed || result != null && !result) {
                failureList.add(entry.getValue());
            }
        }
        return failureList;
    }

    @Override
    public Collection<O> update(Collection<O> objects) {
        Collection<O> failureList = new ArrayList<>();
        RMapAsync<Object, O> mapAsync = redisson.getMap(table);
        Map<RFuture<Boolean>, O> futures = new HashMap<>();
        for (O object : objects) {
            futures.put(mapAsync.fastPutIfExistsAsync(entityToId(object), object), object);
        }
        for (Entry<RFuture<Boolean>, O> entry : futures.entrySet()) {
            RFuture<Boolean> future = entry.getKey();

            boolean failed = false;
            boolean timeot = false;
            Boolean result = null;
            try {
                result = future.get(3000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                timeot = true;
            } catch (Exception e) {
                failed = true;
                LOGGER.error("update objects exception", e);
            }
            if (timeot || failed || result != null && !result) {
                failureList.add(entry.getValue());
            }
        }
        return failureList;
    }

    @Override
    public void delete(O object) {
        redisson.getMap(table)
                .fastRemove(entityToId(object));
    }

    @Override
    public void delete(Collection<O> objects) {
        for (O object : objects) {
            redisson.getMap(table).fastRemove(entityToId(object));
        }
    }

    private Object entityToId(O entity) {
        return idConvertor.entityToId(entity);
    }

    private Object keyToId(K key) {
        return idConvertor.keyToId(key);
    }

    private <C extends Collection<Object>> C keysToIds(Collection<? extends K> keys, C collection) {
        for (K key : keys) {
            Object id = idConvertor.keyToId(key);
            collection.add(id);
        }
        return collection;
    }

}
