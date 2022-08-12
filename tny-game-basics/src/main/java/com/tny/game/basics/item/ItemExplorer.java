/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item;

import java.util.Collection;

/**
 * 事物模型管理器的管理器
 *
 * @author KGTny
 */
public interface ItemExplorer {

    boolean hasItemManager(ItemType itemType);

    <I extends Subject<?>> I getItem(long playerId, int modelId);

    <I extends Subject<?>> I getItem(AnyId anyId);

    boolean insertItem(Subject<?>... items);

    <I extends Subject<?>> Collection<I> insertItem(Collection<I> itemCollection);

    boolean updateItem(Subject<?>... items);

    <I extends Subject<?>> Collection<I> updateItem(Collection<I> itemCollection);

    boolean saveItem(Subject<?>... items);

    <I extends Subject<?>> Collection<I> saveItem(Collection<I> itemCollection);

    void deleteItem(Subject<?>... items);

    <I extends Subject<?>> void deleteItem(Collection<I> itemCollection);

}
