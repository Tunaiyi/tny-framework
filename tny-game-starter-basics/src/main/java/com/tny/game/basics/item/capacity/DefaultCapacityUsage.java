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

import java.util.function.BiFunction;

import static com.tny.game.common.utils.ObjectAide.*;

class DefaultCapacityUsage<T> implements CapacityUsage {

    private final String name;

    private final T defaultValue;

    private final BiFunction<Object, Object, Object> aggregation;

    DefaultCapacityUsage(String name, T defaultNumber, BiFunction<T, T, T> aggregation) {
        this.name = name;
        this.defaultValue = defaultNumber;
        this.aggregation = as(aggregation);
    }

    @Override
    public <V> V aggregate(V one, V other) {
        return as(doAggregate(one, other));
    }

    @Override
    public <V> V defaultValue() {
        return as(defaultValue);
    }

    private Object doAggregate(Object one, Object other) {
        if (one == null && other == null) {
            return defaultValue;
        }
        if (one != null && other == null) {
            return one;
        }
        if (one == null) {
            return other;
        }
        return aggregation.apply(one, other);
    }

    @Override
    public String name() {
        return name;
    }

}
