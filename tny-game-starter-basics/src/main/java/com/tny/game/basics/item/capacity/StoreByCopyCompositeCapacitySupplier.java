/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity;

import com.google.common.base.MoreObjects;
import com.google.common.collect.*;
import com.tny.game.basics.item.*;
import com.tny.game.common.number.*;

import java.util.*;
import java.util.stream.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCompositeCapacitySupplier extends BaseStoreCapable implements CompositeCapacitySupplier, StoreCapacitySupplier {

    private final long id;

    private final int modelId;

    private final CapacitySupplierType type;

    private final Set<Long> suppliers;

    private final CapacityObjectQuerier visitor;

    private final Set<CapacityGroup> groups;

    StoreByCopyCompositeCapacitySupplier(CapacitySupplierType type, long id, int modelId, Stream<Long> suppliers, Stream<CapacityGroup> groups,
            CapacityObjectQuerier visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        this.type = type;
        ImmutableSet.Builder<Long> suppliersBuilder = ImmutableSet.builder();
        suppliers.forEach(suppliersBuilder::add);
        this.suppliers = suppliersBuilder.build();
        ImmutableSet.Builder<CapacityGroup> groupsBuilder = ImmutableSet.builder();
        groups.forEach(groupsBuilder::add);
        this.groups = groupsBuilder.build();
        this.visitor = visitor;
    }

    StoreByCopyCompositeCapacitySupplier(CapacitySupplierType type, long id, int modelId, Stream<? extends CapacitySupplier> suppliers,
            CapacityObjectQuerier visitor, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        this.type = type;
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
    public CapacitySupplierType getSupplierType() {
        return type;
    }

    @Override
    public boolean isHasCapacity(Capacity capacity) {
        if (!this.isWorking() || suppliers.isEmpty()) {
            return false;
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(s -> s.isHasCapacity(capacity));
    }

    @Override
    public Number getCapacity(Capacity capacity, Number defaultValue) {
        if (!this.isWorking() || suppliers.isEmpty()) {
            return defaultValue;
        }
        return suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(s -> s.getCapacity(capacity))
                .filter(Objects::nonNull)
                .reduce(NumberAide::add)
                .orElse(defaultValue);
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        if (!this.isWorking() || suppliers.isEmpty()) {
            return ImmutableSet.of();
        }
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
    public Number getCapacity(Capacity capacity) {
        return this.getCapacity(capacity, null);
    }

    @Override
    public Map<Capacity, Number> getAllCapacities() {
        if (!this.isWorking() || suppliers.isEmpty()) {
            return ImmutableMap.of();
        }
        Map<Capacity, Number> numberMap = new HashMap<>();
        suppliers.stream()
                .map(visitor::findSupplier)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(s -> s.getAllCapacities().forEach((c, num) -> numberMap.merge(c, num, NumberAide::add)));
        return numberMap;
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