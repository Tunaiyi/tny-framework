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
import com.google.common.collect.ImmutableMap;
import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByCopyCapacitySupplier extends BaseStoreCapable implements StoreCapacitySupplier {

    private long id;

    private int modelId;

    private long playerId;

    private CapacitySupplierType type;

    private ImmutableMap<Capacity, Number> capacityMap;

    private Set<CapacityGroup> groups;

    StoreByCopyCapacitySupplier(CapacitySupplierType type, long id, int modelId, long playerId, Map<Capacity, Number> capacityMap,
            Set<CapacityGroup> groups, long expireAt) {
        super(expireAt);
        this.id = id;
        this.modelId = modelId;
        this.playerId = playerId;
        this.type = type;
        this.capacityMap = ImmutableMap.copyOf(capacityMap);
        this.groups = Collections.unmodifiableSet(groups);
    }

    @Override
    public long getId() {
        return this.id;
    }

    public int getModelId() {
        return this.modelId;
    }

    @Override
    public long getPlayerId() {
        return this.playerId;
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return this.type;
    }

    @Override
    public boolean isHasCapacity(Capacity capacity) {
        if (!this.isWorking()) {
            return false;
        }
        return getAllCapacities().containsKey(capacity);
    }

    @Override
    public Number getCapacity(Capacity capacity, Number defaultValue) {
        if (!this.isWorking()) {
            return defaultValue;
        }
        Number number = getAllCapacities().get(capacity);
        return number == null ? defaultValue : number;
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return this.groups;
    }

    @Override
    public Number getCapacity(Capacity capacity) {
        return getCapacity(capacity, null);
    }

    @Override
    public Map<Capacity, Number> getAllCapacities() {
        if (!this.isWorking()) {
            return ImmutableMap.of();
        }
        return this.capacityMap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", this.id)
                .add("modelId", this.modelId)
                .add("name", ItemModels.name(this.modelId))
                .toString();
    }

}