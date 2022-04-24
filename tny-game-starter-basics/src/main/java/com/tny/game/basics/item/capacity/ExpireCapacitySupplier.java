package com.tny.game.basics.item.capacity;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface ExpireCapacitySupplier extends CapacitySupplier, ExpireCapable {

    @Override
    default boolean isWorking() {
        return !isExpire();
    }

}