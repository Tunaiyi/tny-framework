package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;
import com.tny.game.doc.annotation.*;

/**
 * 能力值提供器的游戏能力值
 */
@ClassDoc("游戏能力值")
public interface Capacity extends Ability {

	CapacityUsage getUsage();

	CapacityGroup getGroup();

	default Number getDefault() {
		return 0;
	}

	Number countCapacity(Number baseValue, CapacitySettler settler);

	default Number countFinalCapacity(Item<?> item, Ability ability, CapacitySettler settler) {
		return this.countFinalCapacity(item.getAbility(getDefault(), ability), settler);
	}

	default Number countFinalCapacity(long playerId, ItemModel model, Ability ability, CapacitySettler settler) {
		return this.countFinalCapacity(model.getAbility(playerId, getDefault(), ability), settler);
	}

	default Number countFinalCapacity(CapacitySettler settler) {
		return this.countFinalCapacity(0, settler);
	}

	Number countFinalCapacity(Number baseValue, CapacitySettler settler);

}