package com.tny.game.suite.base.capacity;

import com.tny.game.doc.annotation.ClassDoc;

/**
 * 能力值提供器的游戏能力值
 */
@ClassDoc("游戏能力值")
public interface GameCapacity extends Capacity {

    @Override
    default void registerSelf() {
        Capacity.super.registerSelf();
        Capacities.register(this);
    }

}