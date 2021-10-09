package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class CapacitySupplierItem<IM extends CapacityItemModel> extends GameItem<IM> implements CapacitySupplier {

    protected InnerCapacitySupply capacitySupply;

    @Override
    protected void setModel(IM model) {
        super.setModel(model);
        this.capacitySupply = new DefaultCapacitySupply(this);
    }

    public CapacitySupply supply() {
        return this.capacitySupply;
    }

    @Override
    public abstract long getId();

    protected void refresh() {
        this.capacitySupply.refresh(this);
    }

    protected void invalid() {
        this.capacitySupply.refresh(this);
    }

    protected void effect() {
        this.capacitySupply.effect(this);
    }

}
