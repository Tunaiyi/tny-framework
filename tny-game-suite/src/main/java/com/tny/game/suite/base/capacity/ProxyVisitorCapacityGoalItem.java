package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.*;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public abstract class ProxyVisitorCapacityGoalItem<IM extends ItemModel> extends CapacityGoalItem<IM> implements ProxyVisitorCapacityGoal {

    protected CapacityStorer storer;

    protected ProxyVisitorCapacityGoalItem() {
        super();
    }

    protected ProxyVisitorCapacityGoalItem(InnerCapacityGather capacityGather) {
        super(capacityGather);
    }

    protected void setStorer(CapacityStorer visitor) {
        this.storer = visitor;
    }

    @Override
    public CapacityVisitor visitor() {
        return storer;
    }

    @Override
    protected void accept(CapacitySupplier supplier) {
        this.capacityGather.accept(supplier);
        storer.saveGoal(this.getId(), this.getItemId(), capacityGather);
    }

    @Override
    protected void accept(Collection<? extends CapacitySupplier> suppliers) {
        this.capacityGather.accept(suppliers);
        storer.saveGoal(this.getId(), this.getItemId(), capacityGather);
    }

    @Override
    protected void reduce(CapacitySupplier supplier) {
        this.capacityGather.reduce(supplier);
        storer.saveGoal(this.getId(), this.getItemId(), capacityGather);
    }

    @Override
    protected void reduce(Collection<CapacitySupplier> suppliers) {
        this.capacityGather.reduce(suppliers);
        storer.saveGoal(this.getId(), this.getItemId(), capacityGather);
    }

    @Override
    protected void clear() {
        this.capacityGather.clear();
        storer.saveGoal(this.getId(), this.getItemId(), capacityGather);
    }

    @Override
    protected void invalid() {
        storer.deleteGoal(this);
    }

    @Override
    protected void effect() {
        storer.saveGoal(this.getId(), this.getItemId(), capacityGather);
    }

}
