/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import com.google.common.collect.ImmutableMap;
import com.tny.game.data.*;
import com.tny.game.data.storage.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class GameCacheManager<O> extends GameManager<O> {

    private final EntityCacheManager<AnyId, O> entityManager;

    private EntityOnLoad<AnyId, O> onLoad;

    protected GameCacheManager(Class<? extends O> entityClass, EntityCacheManager<AnyId, O> manager) {
        this(entityClass, manager, null);
    }

    protected GameCacheManager(Class<? extends O> entityClass, EntityCacheManager<AnyId, O> manager, EntityOnLoad<AnyId, O> onLoad) {
        super(entityClass);
        this.entityManager = manager;
        this.onLoad = onLoad;
    }

    @Override
    protected O get(AnyId anyId) {
        return this.entityManager.getEntity(anyId, onLoad);
    }

    @Override
    protected O get(long playerId, long id) {
        AnyId unid = AnyId.idOf(playerId, id);
        return this.entityManager.getEntity(unid, onLoad);
    }

    @Override
    protected List<O> get(Collection<AnyId> anyIdList) {
        return this.entityManager.getEntities(anyIdList, onLoad);
    }

    @Override
    protected List<O> find(Map<String, Object> query) {
        return this.entityManager.find(query, onLoad);
    }

    @Override
    protected List<O> findAll() {
        return this.entityManager.findAll(onLoad);
    }

    protected Collection<O> getByKeys(Collection<AnyId> keys) {
        return this.entityManager.getEntities(keys, onLoad);
    }

    protected Collection<O> getByKeys(AnyId... keys) {
        return this.entityManager.getEntities(Arrays.asList(keys), onLoad);
    }

    protected O getByKey(long playerId, long id) {
        return this.entityManager.getEntity(AnyId.idOf(playerId, id), onLoad);
    }

    protected List<AnyId> findIdList(long playerId) {
        ObjectStorage<AnyId, O> storage = entityManager.getStorage();
        return storage.find(ImmutableMap.of("playerId", playerId), AnyId.class);
    }

    protected List<AnyId> findAllIdList() {
        ObjectStorage<AnyId, O> storage = entityManager.getStorage();
        return storage.findAll(AnyId.class);
    }

    @Override
    public boolean save(O item) {
        return this.entityManager.saveEntity(item);
    }

    @Override
    public Collection<O> save(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.save(o)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean update(O item) {
        return this.entityManager.updateEntity(item);
    }

    @Override
    public Collection<O> update(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.update(o)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean insert(O item) {
        return this.entityManager.insertEntity(item);
    }

    @Override
    public Collection<O> insert(Collection<O> itemCollection) {
        return itemCollection.stream().filter(o -> !this.insert(o)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void delete(O item) {
        this.entityManager.deleteEntity(item);
    }

    @Override
    public void delete(Collection<O> itemCollection) {
        itemCollection.forEach(this::delete);
    }

    protected void onLoad(EntityOnLoad<AnyId, O> onLoad) {
        this.onLoad = onLoad;
    }

}
