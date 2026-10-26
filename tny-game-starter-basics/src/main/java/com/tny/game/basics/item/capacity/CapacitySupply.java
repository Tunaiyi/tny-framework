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

import java.util.Map;

/**
 * 可通过能力接口
 * Created by Kun Yang on 16/3/12.
 */
public interface CapacitySupply extends Capable {

    /**
     * 是否存在 capacity 能力值
     *
     * @param capacity 能力类型
     * @return 存在 capacity 能力值返回 true, 否则返回false
     */
    boolean isHasCapacity(Capacity capacity);

    /**
     * @return 获取所有相关的所有 能力值
     */
    Map<Capacity, Number> getAllCapacities();

}
