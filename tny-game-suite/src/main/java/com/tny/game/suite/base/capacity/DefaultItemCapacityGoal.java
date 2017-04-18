package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Item;

/**
 * Created by Kun Yang on 16/3/11.
 */
public class DefaultItemCapacityGoal extends AbstractCapacityGoal {

    private Item<?> item;

    public DefaultItemCapacityGoal(Item<?> item, CapacityGoalType goalType) {
        super(goalType);
        this.item = item;
    }

    public long getId() {
        return item.getID();
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
        return 0;
    }
}

