package com.tny.game.suite.base.capacity;

import java.util.Set;

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

    default boolean isSupplying() {
        return true;
    }

    /**
     * @return 返回能力提供者类型
     */
    CapacitySupplyType getSupplyType();

    default boolean isCanJoinTo(CapacityGoalType goalType) {
        return getSupplyType().getGoalTypes().contains(goalType);
    }

    /**
     * @return 获取作用目标类型
     */
    default Set<CapacityGoalType> getGoalTypes() {
       return getSupplyType().getGoalTypes();
    }


    default boolean isAllGoal() {
       return getSupplyType().isAll();
    }



//    static boolean isEquals(CapacitySupplier one, Object other) {
//        if (one == other) return true;
//        if (other == null || !(other instanceof CapacitySupplier)) return false;
//        CapacitySupplier that = (CapacitySupplier) other;
//        if (one.getPlayerID() == that.getPlayerID()) return false;
//        if (one.getItemID() != that.getItemID()) return false;
//        return one.getID() == that.getID();
//    }
//
//    static int toHashCode(CapacitySupplier one) {
//        int result = (int) (one.getID() ^ (one.getID() >>> 32));
//        result = 31 * result + one.getItemID();
//        result = 31 * result + (int) (one.getPlayerID() ^ (one.getPlayerID() >>> 32));
//        return result;
//    }

}
