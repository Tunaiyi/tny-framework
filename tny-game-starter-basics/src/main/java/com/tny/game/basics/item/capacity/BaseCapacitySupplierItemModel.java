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
