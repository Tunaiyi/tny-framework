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