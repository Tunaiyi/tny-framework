package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class ProxyGatherCapacityGoalItem<IM extends ItemModel> extends CapacityGoalItem<IM> implements ProxyGatherCapacityGoal {

    protected ProxyGatherCapacityGoalItem() {
        super();
    }

    protected ProxyGatherCapacityGoalItem(InnerCapacityGather capacityGather) {
        super(capacityGather);
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

}
