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

import com.tny.game.basics.item.*;

import java.util.Set;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public interface CapacitySupplierItemModel extends ItemModel {

    /**
     * @return 提供器类型
     */
    CapacitySupplierType getSupplierType();

    /**
     * @return 能力值类型
     */
    Set<Capacity> getCapacities();

    /**
     * @return 能力值组
     */
    Set<CapacityGroup> getCapacityGroups();

}
