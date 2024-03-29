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

package com.tny.game.common.utils;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.StringAide.*;

public class ConfigChecker {

    private static ConcurrentMap<Object, Set<Object>> ID_CHECK_MAP = new ConcurrentHashMap<>();

    private static Set<Object> getCheckSet(Object key) {
        Set<Object> idSet = ID_CHECK_MAP.get(key);
        if (idSet == null) {
            idSet = new HashSet<>();
            Set<Object> oldSet = ID_CHECK_MAP.putIfAbsent(key, idSet);
            if (oldSet != null) {
                idSet = oldSet;
            }
        }
        return idSet;
    }

    public static void check(Object key, Object value, String message, Object... params) {
        Set<Object> idSet = getCheckSet(key);
        if (!idSet.add(value)) {
            IllegalArgumentException e = new IllegalArgumentException(format(message, params));
            e.printStackTrace();
            throw e;
        }
    }

    // public static void checkBehaviorBelogFuncSys(Behavior child, Feature parent) {
    //     if (!(child.getId() + "").startsWith(parent.getId() + "")) {
    //         IllegalArgumentException e = new IllegalArgumentException(format("{} 行为[ID:{}]不属于 {} 系统[ID:{}]",
    //                 child, child.getId(), parent, parent.getId()));
    //         e.printStackTrace();
    //         throw e;
    //     }
    // }
    //
    // public static void checkActionBelogBehavior(Action child, Behavior parent) {
    //     if (child.getId() / 1000 != parent.getId()) {
    //         IllegalArgumentException e = new IllegalArgumentException(format("{} 操作[ID:{}]不属于 {} 行为[ID:{}]",
    //                 child, child.getId(), parent, parent.getId()));
    //         e.printStackTrace();
    //         throw e;
    //     }
    // }

}
