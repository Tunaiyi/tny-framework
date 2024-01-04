/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.tny.game.basics.item.*;

import java.util.*;
import java.util.stream.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCapabler extends BaseStoreCapable implements StoreCapabler {

    private long id;

    private int modelId;

    private Set<Long> suppliers;

    private Set<CapacityGroup> groups;

    private CapacityObjectQuerier visitor;

    StoreByCopyCapabler(long id, int modelId, Stream<Long> suppliers, Stream<CapacityGroup> groups, CapacityObjectQuerier visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        suppliers.forEach(suppliersBuilder::add);
        this.suppliers = suppliersBuilder.build();
        ImmutableSet.Builder<CapacityGroup> groupsBuilder = ImmutableSet.builder();
        groups.forEach(groupsBuilder::add);
        this.groups = groupsBuilder.build();
        this.visitor = visitor;
    }

    StoreByCopyCapabler(long id, int modelId, Stream<? extends CapacitySupplier> suppliers, CapacityObjectQuerier visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<CapacityGroup> groupsBuilder = ImmutableSet.builder();
        suppliers.forEach(s -> {
            suppliersBuilder.add(s.getId());
            groupsBuilder.addAll(s.getAllCapacityGroups());
        });
        this.suppliers = suppliersBuilder.build();
        this.groups = groupsBuilder.build();
        this.visitor = visitor;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getModelId() {
        return modelId;
    }

    @Override
    public long getPlayerId() {
        return visitor.getPlayerId();
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return groups;
    }

    @Override
    public Collection<? extends CapacitySupplier> suppliers() {
        if (suppliers.isEmpty()) {
            return ImmutableList.of();
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Stream<? extends CapacitySupplier> suppliersStream() {
        if (suppliers.isEmpty()) {
            return Stream.empty();
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("modelId", modelId)
                .add("name", ItemModels.name(modelId))
                .add("suppliers", suppliers)
                .toString();
    }

}