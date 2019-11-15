package com.tny.game.suite.base.capacity;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class ProxySupplyCapacitySupplierItem<IM extends CapacityItemModel> extends CapacitySupplierItem<IM>
        implements ProxySupplyCapacitySupplier {

    @Override
    public long getId() {
        return this.getId();
    }

    @Override
    public int getItemId() {
        return this.getItemId();
    }

    @Override
    public long getPlayerId() {
        return this.getPlayerId();
    }


}
