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

import com.tny.game.basics.item.behavior.*;
import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;

import static com.tny.game.scanner.selector.EnumClassSelector.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class GameEnumClassLoader {

    @ClassSelectorProvider
    static ClassSelector itemTypesSelector() {
        return createSelector(ItemType.class, ItemTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector demandTypesSelector() {
        return createSelector(DemandType.class, DemandTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector actionsSelector() {
        return createSelector(Action.class, Actions::register);
    }

    @ClassSelectorProvider
    static ClassSelector behaviorsSelector() {
        return createSelector(Behavior.class, Behaviors::register);
    }

    @ClassSelectorProvider
    static ClassSelector abilitiesSelector() {
        return createSelector(Ability.class, Abilities::register);
    }

    @ClassSelectorProvider
    static ClassSelector demandParamsSelector() {
        return createSelector(DemandParam.class, DemandParams::register);
    }

}
