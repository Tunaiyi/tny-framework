package com.tny.game.basics.item.capacity;

/**
 * 能力值提供七代理接口
 * Created by Kun Yang on 16/3/12.
 */
public interface CompositionCapacitySupplier extends CompositeCapacitySupplier {

	CapacitySupplierComposition composition();

	@Override
	default Number getCapacity(Capacity capacity) {
		return getCapacity(capacity, null);
	}

}
