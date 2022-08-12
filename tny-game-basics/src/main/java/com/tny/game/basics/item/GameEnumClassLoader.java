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
