package com.tny.game.basics.item.capacity.event;

import com.tny.game.basics.item.capacity.*;

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
