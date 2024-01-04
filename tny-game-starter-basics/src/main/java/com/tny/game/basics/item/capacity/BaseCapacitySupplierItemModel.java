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
import com.tny.game.basics.item.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public abstract class BaseCapacitySupplierItemModel extends BaseItemModel implements CapacitySupplierItemModel {

    private Set<Capacity> capacities;

    private Set<CapacityGroup> capacityGroups;

    @Override
    protected void onItemInit(ItemModelContext context) {
        super.onItemInit(context);
        capacities = Collections.unmodifiableSet(this.getAbilityTypes(Capacity.class));
        capacityGroups = Collections.unmodifiableSet(this.capacities.stream()
                .map(Capacity::getGroup)
                .collect(Collectors.toSet()));

    }

    @Override
    public Set<Capacity> getCapacities() {
        return capacities;
    }

    @Override
    public Set<CapacityGroup> getCapacityGroups() {
        return capacityGroups;
    }

}
