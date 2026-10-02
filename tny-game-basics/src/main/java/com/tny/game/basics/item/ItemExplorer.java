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
