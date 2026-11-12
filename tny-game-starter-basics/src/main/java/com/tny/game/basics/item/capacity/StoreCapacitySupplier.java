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

import com.tny.game.common.utils.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface StoreCapacitySupplier extends ExpireCapacitySupplier {

    static StoreCapacitySupplier saveBySupply(CapacitySupplierType type, long id, int modelId, long playerId, CapacitySupply supply,
            long expireAt) {
        return new StoreByCopyCapacitySupplier(type, id, modelId, playerId, supply.getAllCapacities(), supply.getAllCapacityGroups(),
                ExpireCapable.expireAtOf(supply, expireAt));
    }

    static StoreCapacitySupplier saveBySupplier(CapacitySupplier supplier, long expireAt) {
        if (supplier instanceof StoreCapacitySupplier) {
            if (expireAt == 0 || ((StoreCapacitySupplier) supplier).getExpireAt() == expireAt) {
                return ObjectAide.as(supplier);
            }
        }
        return new StoreByCopyCapacitySupplier(
                supplier.getSupplierType(),
                supplier.getId(),
                supplier.getModelId(),
                supplier.getPlayerId(),
                supplier.getAllCapacities(),
                supplier.getAllCapacityGroups(),
                ExpireCapable.expireAtOf(supplier, expireAt));
    }

    static StoreCapacitySupplier saveByCapacities(CapacitySupplierType type, long id, int modelId, long playerId, Map<Capacity, Number> capacityMap,
            Set<CapacityGroup> groups, long expireAt) {
        return new StoreByCopyCapacitySupplier(type, id, modelId, playerId, capacityMap, groups, expireAt > 0 ? expireAt : -1);
    }

    static StoreCapacitySupplier saveBySupplier(CompositeCapacitySupplier supplier, CapacityObjectQuerier visitor, long expireAt) {
        if (supplier instanceof StoreCapacitySupplier) {
            if (expireAt == 0 || ((StoreCapacitySupplier) supplier).getExpireAt() == expireAt) {
                return ObjectAide.as(supplier);
            }
        }
        return saveByDependSuppliers(
                supplier.getSupplierType(),
                supplier.getId(),
                supplier.getModelId(),
                supplier.suppliersStream(),
                visitor,
                ExpireCapable.expireAtOf(supplier, expireAt));
    }

    static StoreCapacitySupplier saveByDependSuppliers(CapacitySupplierType type, long id, int modelId, Stream<? extends CapacitySupplier> suppliers,
            CapacityObjectQuerier visitor, long expireAt) {
        return new StoreByCopyCompositeCapacitySupplier(
                type, id, modelId,
                suppliers.filter(CapacitySupplier::isWorking),
                visitor,
                expireAt > 0 ? expireAt : -1);
    }

    static StoreCapacitySupplier saveByDependSupplierIDs(CapacitySupplierType type, long id, int modelId, Stream<Long> suppliers,
            Stream<CapacityGroup> groups, CapacityObjectQuerier visitor, long expireAt) {
        return new StoreByCopyCompositeCapacitySupplier(type, id, modelId, suppliers, groups, visitor, expireAt > 0 ? expireAt : -1);
    }

    static StoreCapacitySupplier linkBySupplier(CapacitySupplier supplier, long expireAt) {
        if (supplier instanceof StoreCapacitySupplier) {
            if (expireAt == 0 || ((StoreCapacitySupplier) supplier).getExpireAt() == expireAt) {
                return ObjectAide.as(supplier);
            }
        }
        return new StoreByLinkCapacitySupplier(supplier, ExpireCapable.expireAtOf(supplier, expireAt));
    }

    default boolean isLinked() {
        return false;
    }

    void expireAt(long at);

}