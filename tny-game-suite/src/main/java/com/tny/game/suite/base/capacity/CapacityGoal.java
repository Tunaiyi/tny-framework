package com.tny.game.suite.base.capacity;

/**
 * 能力值作用者
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacityGoal extends CapacityGather, Capacitiable {

    /**
     * @return 获取能力提供者ID
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
