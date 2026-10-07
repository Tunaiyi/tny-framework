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
