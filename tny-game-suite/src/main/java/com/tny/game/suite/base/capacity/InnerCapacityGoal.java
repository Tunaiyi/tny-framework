package com.tny.game.suite.base.capacity;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class InnerCapacityGoal implements CapacityGoal {

    /**
     * 接受指定的 supplier
     *
     * @param supplier 指定的 supplier
     */
    public boolean accept(CapacitySupplier supplier) {
        return this.doAccept(supplier);
    }

    public boolean accept(Collection<? extends CapacitySupplier> suppliers) {
        boolean acc = false;
        for (CapacitySupplier supplier : suppliers) {
            if (this.doAccept(supplier))
                acc = true;
        }
        return acc;
    }

    public boolean isCanAccept(CapacitySupplier supplier) {
        return supplier.isAllGoal() || supplier.getGoalTypes().contains(this.getGoalType());
    }

    /**
     * 移除指定的 supplier
     *
     * @param supplier 指定的 supplier
     */
    public boolean reduce(CapacitySupplier supplier) {
        return this.doReduce(supplier);
    }

    public boolean reduce(Collection<? extends CapacitySupplier> suppliers) {
        return suppliers.stream().anyMatch(this::doReduce);
    }

    protected abstract boolean doAccept(CapacitySupplier supplier);

    protected abstract boolean doReduce(CapacitySupplier supplier);

    public abstract void clear();

}
