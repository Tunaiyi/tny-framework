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
