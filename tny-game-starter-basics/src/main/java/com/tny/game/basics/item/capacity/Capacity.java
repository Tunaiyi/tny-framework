/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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