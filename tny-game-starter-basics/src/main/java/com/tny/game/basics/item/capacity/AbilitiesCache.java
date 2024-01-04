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

import java.util.*;

/**
 * 能力值缓存
 */
public interface AbilitiesCache<I extends ItemModel> {

    long getPlayerId();

    I itemModel();

    Number get(Ability ability, Object... attributes);

    Number get(Ability ability, Number defaultNum, Object... attributes);

    <A extends Ability> Map<A, Number> getAll(Class<A> abilityClass, Object... attributes);

    boolean hasAbility(Ability ability);

    Set<Ability> getAllAbilityTypes();

    <S extends Ability> Set<S> getAbilityTypes(Class<S> abilityClass);

    void refresh();

}
