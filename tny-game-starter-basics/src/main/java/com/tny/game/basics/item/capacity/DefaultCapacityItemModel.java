package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public abstract class DefaultCapacityItemModel extends BaseItemModel implements CapacityItemModel {

	private Set<Capacity> capacities;

	private Set<CapacityGroup> capacityGroups;

	@Override
	protected void onItemInit(ItemModelContext context) {
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
