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
