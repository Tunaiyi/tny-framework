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

import com.tny.game.common.number.*;

import java.util.*;

/**
 * 组合能力提供器
 * Created by Kun Yang on 16/4/12.
 */
public interface CompositeCapacitySupplier extends CapacitySupplier, CapableComposition {

    @Override
    default boolean isHasCapacity(Capacity capacity) {
        return isWorking() && suppliersStream().anyMatch(s -> s.isHasCapacity(capacity));
    }

    @Override
    default Map<Capacity, Number> getAllCapacities() {
        Map<Capacity, Number> numberMap = new HashMap<>();
        suppliers()
                .forEach(s -> s.getAllCapacities().forEach((c, num) -> numberMap.merge(c, num, NumberAide::add)));
        return numberMap;
    }

}
