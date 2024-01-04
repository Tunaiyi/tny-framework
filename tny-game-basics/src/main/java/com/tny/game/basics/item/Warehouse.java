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

package com.tny.game.basics.item;

import java.util.function.BiFunction;

/**
 * 残酷系统
 *
 * @author KGTny
 */
public interface Warehouse extends Any {

    /**
     * 获取指定itemType的Storage对象
     *
     * @param itemType 事物类型
     * @return 返回storage对象
     */
    <O extends StuffOwner<?, ?>> O loadOwner(ItemType itemType, BiFunction<Warehouse, ItemType, O> ownerSupplier);

}
