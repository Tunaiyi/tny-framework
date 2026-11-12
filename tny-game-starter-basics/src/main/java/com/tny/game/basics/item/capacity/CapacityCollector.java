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
 * Created by Kun Yang on 2017/7/17.
 */
public class CapacityCollector {

    private Map<Capacity, Number> capacities = new HashMap<>();

    private Map<Capacity, Number> visitCapacities = Collections.unmodifiableMap(capacities);

    public void collect(Capacity capacity, Number number) {
        if (number == null) {
            return;
        }
        capacities.merge(capacity, number, NumberAide::add);
    }

    public Map<Capacity, Number> getCapacities() {
        return visitCapacities;
    }

}
