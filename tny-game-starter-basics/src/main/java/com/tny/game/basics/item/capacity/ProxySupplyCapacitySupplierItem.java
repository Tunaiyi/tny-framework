package com.tny.game.basics.item.capacity;

/**
 * Item能力值提供器
 * Created by Kun Yang on 16/3/12.
 */
public abstract class ProxySupplyCapacitySupplierItem<IM extends CapacityItemModel> extends CapacitySupplierItem<IM>
		implements ProxySupplyCapacitySupplier {

	@Override
	public int getModelId() {
		return super.getModelId();
	}

	@Override
	public long getPlayerId() {
		return super.getPlayerId();
	}

}
