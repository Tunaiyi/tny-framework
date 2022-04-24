package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class CapacitySupplierItem<IM extends CapacitySupplierItemModel> extends BaseItem<IM> implements CapacitySupplier {

    protected CapacitySupplierItem() {
    }

    protected CapacitySupplierItem(long playerId, IM model) {
        super(playerId, model);
    }

    @Override
    public CapacitySupplierType getSupplierType() {
        return model.getSupplierType();
    }

    protected abstract void refresh();

    protected abstract void invalid();

    protected abstract void effect();

}
