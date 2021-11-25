package com.tny.game.basics.item.capacity;

import com.tny.game.common.enums.*;

import java.util.List;

/**
 * Created by Kun Yang on 2017/4/7.
 */
public interface CapacityGroup extends Enumerable<Integer> {

	List<Capacity> getCapacities();

	default void registerSelf() {
		Capacities.register(this);
	}

}
