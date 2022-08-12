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
            if (expireAt == 0 || ((StoreCapacitySupplier)supplier).getExpireAt() == expireAt) {
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
            if (expireAt == 0 || ((StoreCapacitySupplier)supplier).getExpireAt() == expireAt) {
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
            if (expireAt == 0 || ((StoreCapacitySupplier)supplier).getExpireAt() == expireAt) {
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