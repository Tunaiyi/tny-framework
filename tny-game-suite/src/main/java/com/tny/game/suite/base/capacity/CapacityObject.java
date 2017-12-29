package com.tny.game.suite.base.capacity;

/**
 * 能力值相关对象
 */
public interface CapacityObject {

    /**
     * @return 获取ID
     */
    long getID();

    /**
     * @return 获取能力提供者ID
     */
    int getItemID();

    /**
     * @return 玩家ID
     */
    long getPlayerID();

}