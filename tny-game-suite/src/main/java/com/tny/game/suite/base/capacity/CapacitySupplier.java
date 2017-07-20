package com.tny.game.suite.base.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacitySupplier extends CapacitySupply, Capacitiable {

    /**
     * @return 获取能力提供者ID
     */
    long getID();

    /**
     * @return 获取ItemID
     */
    int getItemID();

    /**
     * @return 获取玩家ID
     */
    long getPlayerID();

    /**
     * @return 是否提供
     */
    default boolean isSupplying() {
        return true;
    }

    /**
     * @return 返回能力提供者类型
     */
    CapacitySupplierType getSupplierType();

}
