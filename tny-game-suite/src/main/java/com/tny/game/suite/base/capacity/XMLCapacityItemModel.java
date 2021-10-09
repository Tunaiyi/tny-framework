package com.tny.game.suite.base.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.xml.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public abstract class XMLCapacityItemModel extends XMLItemModel implements CapacityItemModel {

    private Set<Capacity> capacities;

    private Set<CapacityGroup> capacityGroups;

    @Override
    protected void init(ItemModelContext context) {
        super.init(context);
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
