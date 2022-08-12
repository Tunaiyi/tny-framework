/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.mould;

import com.tny.game.scanner.*;
import com.tny.game.scanner.annotation.*;
import org.slf4j.*;

import static com.tny.game.scanner.selector.EnumClassSelector.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/25 9:46 下午
 */
public class FeatureEnumClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(FeatureEnumClassLoader.class);

    @ClassSelectorProvider
    static ClassSelector openModesSelector() {
        return createSelector(FeatureOpenMode.class, FeatureOpenModes::register);
    }

    @ClassSelectorProvider
    static ClassSelector featuresSelector() {
        return createSelector(Feature.class, Features::register);
    }

    @ClassSelectorProvider
    static ClassSelector mouldsSelector() {
        return createSelector(Mould.class, Moulds::register);
    }

}
