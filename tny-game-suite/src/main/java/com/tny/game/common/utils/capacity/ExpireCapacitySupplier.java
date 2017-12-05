package com.tny.game.common.utils.capacity;


/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface ExpireCapacitySupplier extends CapacitySupplier, ExpireCapacitiable {

    @Override
    default boolean isSupplying() {
        return !isExpire();
    }
}