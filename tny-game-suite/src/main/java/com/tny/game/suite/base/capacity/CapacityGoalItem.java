package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.ItemModel;
import com.tny.game.suite.base.GameItem;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class CapacityGoalItem<IM extends ItemModel> extends GameItem<IM> implements ProxyCapacityGoal {

    protected InnerCapacityGoal capacityGoal;

    protected CapacityGoalItem(InnerCapacityGoal capacityGoal) {
        this.capacityGoal = capacityGoal;
    }

    @Override
    public abstract long getID();

    @Override
    public CapacityGoal capacityGoal() {
        return capacityGoal;
    }

    protected void accept(CapacitySupplier supplier) {
        this.capacityGoal.accept(supplier);
    }

    protected void accept(Collection<? extends CapacitySupplier> suppliers) {
        this.capacityGoal.accept(suppliers);
    }

    protected void reduce(CapacitySupplier supplier) {
        this.capacityGoal.reduce(supplier);
    }

    protected void reduce(Collection<CapacitySupplier> suppliers) {
        this.capacityGoal.reduce(suppliers);
    }

    protected void clear() {
        this.capacityGoal.clear();
    }

}
