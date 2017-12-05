package com.tny.game.common.utils.capacity;

import com.tny.game.base.item.ItemExplorer;
import com.tny.game.base.item.ModelExplorer;
import com.tny.game.base.item.xml.XMLItemModel;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public abstract class XMLCapacityItemModel extends XMLItemModel implements CapacityItemModel {

    private Set<Capacity> capacities;

    private Set<CapacityGroup> capacityGroups;

    @Override
    protected void init(ItemExplorer itemExplorer, ModelExplorer itemModelExplorer) {
        super.init(itemExplorer, itemModelExplorer);
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
