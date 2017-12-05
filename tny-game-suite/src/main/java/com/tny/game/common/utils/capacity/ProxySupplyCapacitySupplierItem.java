package com.tny.game.common.utils.capacity;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class ProxySupplyCapacitySupplierItem<IM extends CapacityItemModel> extends CapacitySupplierItem<IM> implements ProxySupplyCapacitySupplier {

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
