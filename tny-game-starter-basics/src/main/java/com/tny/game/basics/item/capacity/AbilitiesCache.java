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
