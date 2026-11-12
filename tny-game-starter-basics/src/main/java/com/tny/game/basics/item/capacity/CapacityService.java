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

import java.util.*;

/**
 * 游戏能力值Service
 * Created by Kun Yang on 16/2/17.
 */
public class CapacityService {

    public void accept(BaseCapablerItem<?> goal, CapacitySupplier... suppliers) {
        this.accept(goal, Arrays.asList(suppliers));
    }

    public void accept(BaseCapablerItem<?> goal, Collection<CapacitySupplier> suppliers) {
        goal.accept(suppliers);
    }

    public void reduce(BaseCapablerItem<?> goal, CapacitySupplier... suppliers) {
        this.reduce(goal, Arrays.asList(suppliers));
    }

    public void reduce(BaseCapablerItem<?> goal, Collection<CapacitySupplier> suppliers) {
        goal.remove(suppliers);
    }

}
