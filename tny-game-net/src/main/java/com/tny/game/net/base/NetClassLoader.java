/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.base;

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
public class NetClassLoader {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetClassLoader.class);

    @ClassSelectorProvider
    static ClassSelector appTypeSelector() {
        return createSelector(AppType.class, AppTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector scopeTypeSelector() {
        return createSelector(AppScope.class, AppScopes::register);
    }

    @ClassSelectorProvider
    static ClassSelector messagerTypesSelector() {
        return createSelector(MessagerType.class, MessagerTypes::register);
    }

    @ClassSelectorProvider
    static ClassSelector serviceTypesSelector() {
        return createSelector(RpcServiceType.class, RpcServiceTypes::register);
    }

}
