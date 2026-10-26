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
