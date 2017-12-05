package com.tny.game.common.utils.capacity.event;


import com.tny.game.common.utils.capacity.CapacitySupplier;
import com.tny.game.common.utils.capacity.CapacitySupply;

/**
 * Created by Kun Yang on 16/2/17.
 */
public interface CapacityListener {

    default void onChange(CapacitySupply source, CapacitySupplier owner){}

    default void onInvalid(CapacitySupply source, CapacitySupplier owner){}

    default void onEffect(CapacitySupply source, CapacitySupplier owner){}

}
