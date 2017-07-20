package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.Ability;
import com.tny.game.base.item.Item;
import com.tny.game.base.item.ItemModel;
import com.tny.game.doc.annotation.ClassDoc;
import com.tny.game.suite.base.GameAbility;

/**
 * 能力值提供器的游戏能力值
 */
@ClassDoc("游戏能力值")
public interface Capacity extends GameAbility {

    CapacityValueType getValueType();

    CapacityGroup getGroup();


    default Number getDefault() {
        return 0;
    }

    default Number getBaseCapacity(Number baseValue, CapacityGather gather) {
        return CapacityUtils.countCapacity(baseValue, gather, this);
    }

    default Number countFinalCapacity(Item<?> item, Ability ability, CapacityGather gather) {
        return this.countFinalCapacity(item.getAbility(getDefault(), ability), gather);
    }

    default Number countFinalCapacity(long playerID, ItemModel model, Ability ability, CapacityGather gather) {
        return this.countFinalCapacity(model.getAbility(playerID, getDefault(), ability), gather);
    }

    default Number countFinalCapacity(CapacityGather gather) {
        return this.countFinalCapacity(0, gather);
    }

    Number countFinalCapacity(Number baseValue, CapacityGather gather);

    @Override
    default void registerSelf() {
        GameAbility.super.registerSelf();
        Capacities.register(this);
    }

}