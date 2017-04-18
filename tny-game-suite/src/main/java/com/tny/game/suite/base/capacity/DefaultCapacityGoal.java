package com.tny.game.suite.base.capacity;

import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/3/11.
 */
public class DefaultCapacityGoal extends AbstractCapacityGoal {

    private Supplier<Long> idSupplier;

    public DefaultCapacityGoal(long id, CapacityGoalType goalType) {
        super(goalType);
        this.idSupplier = () -> id;
    }

    public DefaultCapacityGoal(Supplier<Long> idSupplier, CapacityGoalType goalType) {
        super(goalType);
        this.idSupplier = idSupplier;
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

    @Override
    public long getID() {
        return idSupplier.get();
    }
}

