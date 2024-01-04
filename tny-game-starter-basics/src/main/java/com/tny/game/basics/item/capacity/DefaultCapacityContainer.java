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

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.event.*;

import java.util.*;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class DefaultCapacityContainer implements CapacityContainer {

    private long playerId;

    private CapacitySupplierItemModel model;

    private Item<?> item;

    public DefaultCapacityContainer(Item<?> item, CapacitySupplierItemModel model) {
        this.playerId = item.getPlayerId();
        this.item = item;
        this.model = model;
    }

    public DefaultCapacityContainer(Item<? extends CapacitySupplierItemModel> item) {
        this.playerId = item.getPlayerId();
        this.model = item.getModel();
        this.item = item;
    }

    public DefaultCapacityContainer(long playerId, CapacitySupplierItemModel model) {
        this.playerId = playerId;
        this.model = model;
    }

    @Override
    public Number getCapacity(Capacity capacity, Number defaultNum) {
        if (this.item != null) {
            return this.model.getAbility(this.item, defaultNum, capacity);
        } else {
            return this.model.getAbility(this.playerId, defaultNum, capacity);
        }
    }

    @Override
    public Set<CapacityGroup> getAllCapacityGroups() {
        return this.model.getCapacityGroups();
    }

    @Override
    public Number getCapacity(Capacity capacity) {
        if (this.item != null) {
            return this.model.getAbility(this.item, capacity, Number.class);
        } else {
            return this.model.getAbility(this.playerId, capacity, Number.class);
        }
    }

    @Override
    public Map<Capacity, Number> getAllCapacities() {
        if (this.item != null) {
            return this.model.getAbilitiesByType(this.item, Capacity.class, Number.class);
        } else {
            return this.model.getAbilitiesByType(this.playerId, Capacity.class, Number.class);
        }
    }

    @Override
    public boolean isHasCapacity(Capacity capacity) {
        return this.model.hasAbility(capacity);
    }

    @Override
    public void refresh(CapacitySupplier supplier) {
        CapacityEvents.ON_CHANGE.notify(this, supplier);
    }

    @Override
    public void invalid(CapacitySupplier supplier) {
        CapacityEvents.ON_INVALID.notify(this, supplier);
    }

    @Override
    public void effect(CapacitySupplier supplier) {
        CapacityEvents.ON_EFFECT.notify(this, supplier);
    }

}
