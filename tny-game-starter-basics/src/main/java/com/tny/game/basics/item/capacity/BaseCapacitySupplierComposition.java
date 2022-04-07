package com.tny.game.basics.item.capacity;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class BaseCapacitySupplierComposition implements CapacitySupplierComposition {

	protected volatile Set<CapacitySupplier> suppliers = new CopyOnWriteArraySet<>();

	@Override
	public Collection<? extends CapacitySupplier> suppliers() {
		return Collections.unmodifiableCollection(suppliers);
	}

	@Override
	public boolean doAccept(CapacitySupplier supplier) {
		this.suppliers.remove(supplier);
		return suppliers.add(supplier);
	}

	@Override
	public boolean doRemove(CapacitySupplier supplier) {
		return suppliers.remove(supplier);
	}

	@Override
	public void clear() {
		this.suppliers = new CopyOnWriteArraySet<>();
	}

	@Override
	public void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
		suppliers.forEach(supplier -> supplier.collectCapacities(collector, capacities));
	}

}
