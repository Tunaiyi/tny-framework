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
 * Storage总管理器
 *
 * @author KGTny
 */
public interface StuffOwnerExplorer {

    <O extends StuffOwner<?, ?>> O getOwner(long playerId, ItemType ownerType);

    boolean insertOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> insertOwner(Collection<O> storageCollection);

    boolean updateOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> updateOwner(Collection<O> storageCollection);

    boolean saveOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> saveOwner(Collection<O> storageCollection);

    void deleteOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> void deleteOwner(Collection<O> storageCollection);

}