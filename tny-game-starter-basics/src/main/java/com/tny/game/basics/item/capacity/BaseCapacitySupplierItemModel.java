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
