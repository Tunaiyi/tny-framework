package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.ItemModel;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class ProxySupplyCapacitySupplierItem<IM extends ItemModel> extends CapacitySupplierItem<IM> implements ProxySupplyCapacitySupplier {

    @Override
    public long getID() {
        return this.getID();
    }

    @Override
    public int getItemID() {
        return this.getItemID();
    }

    @Override
    public long getPlayerID() {
        return this.getPlayerID();
    }


}
