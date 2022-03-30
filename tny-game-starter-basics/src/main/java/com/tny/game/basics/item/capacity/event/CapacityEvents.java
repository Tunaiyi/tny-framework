package com.tny.game.basics.item.capacity.event;

import com.tny.game.basics.item.capacity.*;
import com.tny.game.common.event.bus.*;

/**
 * Created by Kun Yang on 16/2/17.
 */
public interface CapacityEvents {

	BindP1EventBus<CapacityListener, CapacitySupply, CapacitySupplier>
			ON_CHANGE = EventBuses.of(CapacityListener.class, CapacityListener::onChange);

	BindP1EventBus<CapacityListener, CapacitySupply, CapacitySupplier>
			ON_INVALID = EventBuses.of(CapacityListener.class, CapacityListener::onInvalid);

	BindP1EventBus<CapacityListener, CapacitySupply, CapacitySupplier>
			ON_EFFECT = EventBuses.of(CapacityListener.class, CapacityListener::onEffect);

}
