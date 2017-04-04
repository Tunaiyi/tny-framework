package com.tny.game.suite.base.capacity;

/**
 * Created by Kun Yang on 16/3/11.
 */
public class DefaultCapacityGoal extends AbstractCapacityGoal {

    public DefaultCapacityGoal(CapacityGoalType goalType) {
        super(goalType);
    }

    @Override
    public boolean isCanAccept(CapacitySupplier supplier) {
        return supplier.isAllGoal() || supplier.getGoalTypes().contains(this.getGoalType());
    }

    @Override
    protected boolean doAccept(CapacitySupplier supplier) {
        if (isCanAccept(supplier)) {
            this.suppliers.remove(supplier);
            return suppliers.add(supplier);
        }
        return false;
    }

    @Override
    protected boolean doReduce(CapacitySupplier supplier) {
        return suppliers.remove(supplier);
    }

}

