/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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