package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.basics.item.capacity.event.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 可缓存能力提供器
 * Created by Kun Yang on 16/3/12.
 */
public class CachedCapacityContainer implements CapacityContainer {

	private AbilitiesCache<? extends CapacitySupplierItemModel> cache;

	public CachedCapacityContainer(Item<? extends CapacitySupplierItemModel> item) {
		this(new DefaultAbilitiesCache<>(item));
	}

	public CachedCapacityContainer(long playerId, CapacitySupplierItemModel model) {
		this(new DefaultAbilitiesCache<>(playerId, model));
	}

	public CachedCapacityContainer(Item<?> item, CapacitySupplierItemModel model) {
		this(new DefaultAbilitiesCache<>(item, model));
	}

	private CachedCapacityContainer(AbilitiesCache<? extends CapacitySupplierItemModel> cache) {
		this.cache = cache;
	}

	@Override
	public Number getCapacity(Capacity capacity) {
		return this.cache.get(capacity);
	}

	@Override
	public Number getCapacity(Capacity capacity, Number defaultNum) {
		return this.cache.get(capacity, defaultNum);
	}

	@Override
	public void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
		for (Capacity capacity : capacities) {
			collector.collect(capacity, this.cache.get(capacity));
		}
	}

	@Override
	public Set<CapacityGroup> getAllCapacityGroups() {
		return this.cache.itemModel().getCapacityGroups();
	}

	@Override
	public Map<Capacity, Number> getAllCapacities() {
		return this.cache.getAll(Capacity.class).entrySet()
				.stream()
				.collect(Collectors.toMap(
						e -> (Capacity)e.getKey(),
						Entry::getValue));
	}

	@Override
	public boolean isHasCapacity(Capacity capacity) {
		return this.cache.hasAbility(capacity);
	}

	@Override
	public void refresh(CapacitySupplier supplier) {
		this.cache.refresh();
		CapacityEvents.ON_CHANGE.notify(this, supplier);
	}

	@Override
	public void invalid(CapacitySupplier supplier) {
		this.cache.refresh();
		CapacityEvents.ON_INVALID.notify(this, supplier);
	}

	@Override
	public void effect(CapacitySupplier supplier) {
		this.cache.refresh();
		CapacityEvents.ON_EFFECT.notify(this, supplier);
	}

}
