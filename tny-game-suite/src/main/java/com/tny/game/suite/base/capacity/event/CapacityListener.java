package com.tny.game.suite.base.capacity.event;


import com.tny.game.suite.base.capacity.CapacitySupplier;
import com.tny.game.suite.base.capacity.CapacitySupply;

/**
 * Created by Kun Yang on 16/2/17.
 */
public interface CapacityListener {

    void onChange(CapacitySupply source, CapacitySupplier owner);

    void onInvalid(CapacitySupply source, CapacitySupplier owner);

    void onEffect(CapacitySupply source, CapacitySupplier owner);

}
