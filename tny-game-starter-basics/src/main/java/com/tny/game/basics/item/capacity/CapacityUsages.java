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

import com.tny.game.common.utils.*;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class CapacityUsages {

    private static final ConcurrentMap<String, CapacityUsage> NAME_TYPE_MAP = new ConcurrentHashMap<>();

    public static <T> CapacityUsage usageOf(String name, T defaultNumber, BiFunction<T, T, T> aggregation) {
        CapacityUsage type = new DefaultCapacityUsage<>(name, defaultNumber, aggregation);
        CapacityUsage old = NAME_TYPE_MAP.putIfAbsent(type.name(), type);
        if (old != null) {
            throw new IllegalArgumentException(StringAide.format("创建{}失败, {} : {} 已存在", type, name, old));
        }
        return type;
    }

    public static Collection<CapacityUsage> all() {
        return NAME_TYPE_MAP.values();
    }

}
