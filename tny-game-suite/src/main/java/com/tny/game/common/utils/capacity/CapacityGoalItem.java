package com.tny.game.common.utils.capacity;

import com.tny.game.base.item.ItemModel;
import com.tny.game.common.utils.GameItem;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class CapacityGoalItem<IM extends ItemModel> extends GameItem<IM> implements CapacityGoal {

    protected InnerCapacityGather capacityGather;

    protected CapacityGoalItem() {
        capacityGather = new DefaultCapacityGather();
    }

    protected CapacityGoalItem(InnerCapacityGather capacityGather) {
        this.capacityGather = capacityGather;
    }

    public CapacityGather gather() {
        return capacityGather;
    }

    protected CapacityGoalItem setCapacityGather(InnerCapacityGather capacityGather) {
        this.capacityGather = capacityGather;
        return this;
    }

    protected void accept(CapacitySupplier supplier) {
        this.capacityGather.accept(supplier);
    }

    protected void accept(Collection<? extends CapacitySupplier> suppliers) {
        this.capacityGather.accept(suppliers);
    }

    protected void reduce(CapacitySupplier supplier) {
        this.capacityGather.reduce(supplier);
    }

    protected void reduce(Collection<CapacitySupplier> suppliers) {
        this.capacityGather.reduce(suppliers);
    }

    protected void clear() {
        this.capacityGather.clear();
    }

    protected void invalid() {
    }

    protected void effect() {
    }

}
