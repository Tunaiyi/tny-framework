package com.tny.game.common.utils.capacity.event;

import com.tny.game.common.event.BindP1EventBus;
import com.tny.game.common.event.EventBuses;
import com.tny.game.common.utils.capacity.CapacitySupplier;
import com.tny.game.common.utils.capacity.CapacitySupply;

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
