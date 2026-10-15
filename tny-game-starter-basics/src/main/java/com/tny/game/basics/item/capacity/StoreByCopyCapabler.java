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