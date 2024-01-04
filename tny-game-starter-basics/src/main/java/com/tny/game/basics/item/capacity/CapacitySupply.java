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
