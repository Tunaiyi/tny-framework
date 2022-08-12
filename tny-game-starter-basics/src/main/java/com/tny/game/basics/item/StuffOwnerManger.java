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

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/25 4:33 下午
 */
public interface StuffOwnerManger<O extends StuffOwner<?, ?>> extends Manager<O>, ItemTypesManager {

    O getOwner(long playerId);

    ItemType getOwnerItemType();

    @Override
    default Set<ItemType> manageTypes() {
        return ImmutableSet.of(getOwnerItemType());
    }

}
