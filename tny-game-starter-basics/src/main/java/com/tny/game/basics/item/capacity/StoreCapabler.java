package com.tny.game.basics.item.capacity;

import com.tny.game.common.utils.*;

import java.util.stream.Stream;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface StoreCapabler extends ExpireCapabler {

    static StoreCapabler saveByCapabler(Capabler goal, CapacityObjectQuerier visitor, long expireAt) {
        if (goal instanceof StoreCapabler) {
            if (expireAt == 0 || ((StoreCapabler)goal).getExpireAt() == expireAt) {
                return ObjectAide.as(goal);
            }
        }
        return saveBySuppliers(
                goal.getId(),
                goal.getModelId(),
                goal.suppliersStream(),
                visitor,
                ExpireCapable.expireAtOf(goal, expireAt));
    }

    static StoreCapabler saveByComposition(long id, int modelId, CapableComposition composition, CapacityObjectQuerier visitor,
            long expireAt) {
        return new StoreByCopyCapabler(
                id, modelId,
                composition.suppliersStream().filter(CapacitySupplier::isWorking),
                visitor,
                ExpireCapable.expireAtOf(composition, expireAt));
    }

    static StoreCapabler saveBySuppliers(long id, int modelId, Stream<? extends CapacitySupplier> suppliers, CapacityObjectQuerier visitor,
            long expireAt) {
        return new StoreByCopyCapabler(
                id, modelId,
                suppliers.filter(CapacitySupplier::isWorking),
                visitor,
                expireAt > 0 ? expireAt : -1);
    }

    static StoreCapabler saveBySupplierIds(long id, int modelId, Stream<Long> suppliers, Stream<CapacityGroup> groups, CapacityObjectQuerier visitor,
            long expireAt) {
        return new StoreByCopyCapabler(id, modelId, suppliers, groups, visitor, expireAt > 0 ? expireAt : -1);
    }

    void expireAt(long at);

}