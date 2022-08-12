/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.data.redisson;

import com.tny.game.data.*;
import com.tny.game.data.accessor.*;
import com.tny.game.redisson.*;
import org.redisson.api.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/27 12:15 下午
 */
public class RedissonStorageAccessor<K extends Comparable<?>, O> implements StorageAccessor<K, O> {

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
            if (!future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS) || !future.getNow()) {
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
            if (!future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS) || !future.getNow()) {
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
            if (!future.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS) || !future.getNow()) {
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

    @Override
    public void execute() {
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
