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

    default Number getDefault() {
        return 0;
    }

    default Number getBaseCapacity(Number baseValue, CapacityGoal goal) {
        return CapacityUtils.countCapacity(baseValue, goal, this);
    }

    default Number countFinalCapacity(Item<?> item, Ability ability, CapacityGoal goal) {
        return this.countFinalCapacity(item.getAbility(getDefault(), ability), goal);
    }

    default Number countFinalCapacity(long playerID, ItemModel model, Ability ability, CapacityGoal goal) {
        return this.countFinalCapacity(model.getAbility(playerID, getDefault(), ability), goal);
    }

    default Number countFinalCapacity(CapacityGoal goal) {
        return this.countFinalCapacity(0, goal);
    }

    Number countFinalCapacity(Number baseValue, CapacityGoal goal);

}