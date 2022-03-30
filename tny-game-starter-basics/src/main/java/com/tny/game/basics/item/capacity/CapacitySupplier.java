package com.tny.game.basics.item.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacitySupplier extends CapacitySupply, CapacityObject {

	/**
	 * @return 是否提供
	 */
	default boolean isWorking() {
		return true;
	}

	/**
	 * @return 返回能力提供者类型
	 */
	CapacitySupplierType getSupplierType();

}
