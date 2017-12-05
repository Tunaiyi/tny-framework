package com.tny.game.common.utils.capacity;

/**
 * Created by Kun Yang on 16/3/11.
 */
public class DefaultCapacityGather extends AbstractCapacityGather {

    @Override
    public boolean doAccept(CapacitySupplier supplier) {
        this.suppliers.remove(supplier);
        return suppliers.add(supplier);
    }

    @Override
    public boolean doReduce(CapacitySupplier supplier) {
        return suppliers.remove(supplier);
    }

}

