/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.xml;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class AliasCollectUtils {

    private static Set<String> aliasSet = new ConcurrentSkipListSet<>();

    public static void addAlias(String alias) {
        if (alias == null || alias.equalsIgnoreCase("null")) {
            throw new IllegalArgumentException("alias is " + alias);
        }
        aliasSet.add(alias);
    }

    public static Set<String> getAllAlias() {
        return aliasSet;
    }

}
