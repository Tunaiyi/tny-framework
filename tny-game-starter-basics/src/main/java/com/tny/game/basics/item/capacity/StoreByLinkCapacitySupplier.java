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
import com.tny.game.basics.item.*;

import java.util.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public class StoreByLinkCapacitySupplier extends BaseStoreCapable implements StoreCapacitySupplier {

    private CapacitySupplier supplier;

    StoreByLinkCapacitySupplier(CapacitySupplier supplier, long expireAt) {
        super(expireAt);
        this.supplier = supplier;
    }

    @Override
    public long getId() {
        return supplier.getId();
    }

    public int getModelId() {
        return supplier.getModelId();
    }

    @Override
    public long getPlayerId() {
        return supplier.getPlayerId();
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return supplier.getSupplierType();
    }

    @Override
    public boolean isHasCapacity(Capacity capacity) {
        return supplier.isHasCapacity(capacity);
    }

    @Override
    public Number getCapacity(Capacity capacity, Number defaultValue) {
        return supplier.getCapacity(capacity, defaultValue);
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return supplier.getAllCapacityGroups();
    }

    @Override
    public Number getCapacity(Capacity capacity) {
        return getCapacity(capacity, null);
    }

    @Override
    public Map<Capacity, Number> getAllCapacities() {
        return supplier.getAllCapacities();
    }

    @Override
    public boolean isLinked() {
        return true;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", supplier.getId())
                .add("modelId", supplier.getModelId())
                .add("name", ItemModels.name(supplier.getModelId()))
                .toString();
    }

}