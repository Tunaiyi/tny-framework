package com.tny.game.suite.base.capacity;


import com.tny.game.common.utils.*;

import java.util.stream.Stream;

import static com.tny.game.suite.base.capacity.ExpireCapacitiable.*;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface StoreCapacityGoal extends ExpireCapacityGoal {


    static StoreCapacityGoal saveByGoal(CapacityGoal goal, CapacityVisitor visitor, long expireAt) {
        if (goal instanceof StoreCapacityGoal) {
            if (expireAt == 0 || ((StoreCapacityGoal) goal).getExpireAt() == expireAt)
                return ObjectAide.as(goal);
        }
        return saveBySuppliers(
                goal.getId(),
                goal.getItemId(),
                goal.suppliersStream(),
                visitor,
                expireAtOf(goal, expireAt));
    }

    static StoreCapacityGoal saveByGather(long id, int itemID, CapacityGather gather, CapacityVisitor visitor, long expireAt) {
        return new StoreByCopyCapacityGoal(
                id, itemID,
                gather.suppliersStream()
                      .filter(CapacitySupplier::isSupplying),
                visitor,
                expireAtOf(gather, expireAt));
    }

    static StoreCapacityGoal saveBySuppliers(long id, int itemID, Stream<? extends CapacitySupplier> suppliers, CapacityVisitor visitor,
            long expireAt) {
        return new StoreByCopyCapacityGoal(
                id, itemID,
                suppliers.filter(CapacitySupplier::isSupplying),
                visitor,
                expireAt > 0 ? expireAt : -1);
    }

    static StoreCapacityGoal saveBySupplierIDs(long id, int itemID, Stream<Long> suppliers, Stream<CapacityGroup> groups, CapacityVisitor visitor,
            long expireAt) {
        return new StoreByCopyCapacityGoal(id, itemID, suppliers, groups, visitor, expireAt > 0 ? expireAt : -1);
    }


    void expireAt(long at);

}