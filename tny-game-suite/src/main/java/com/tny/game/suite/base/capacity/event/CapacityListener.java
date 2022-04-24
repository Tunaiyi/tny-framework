package com.tny.game.suite.base.capacity.event;

import com.tny.game.suite.base.capacity.*;

/**
 * Created by Kun Yang on 16/2/17.
 */
public interface CapacityListener {

    default void onChange(CapacitySupply source, CapacitySupplier owner) {
    }

    default void onInvalid(CapacitySupply source, CapacitySupplier owner) {
    }

    default void onEffect(CapacitySupply source, CapacitySupplier owner) {
    }

}
