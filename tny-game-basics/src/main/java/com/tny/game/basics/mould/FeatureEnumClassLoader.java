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
