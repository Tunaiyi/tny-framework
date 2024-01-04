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

package com.tny.game.common.collection.map.access;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/3 8:12 下午
 */
public class MapAccessors {

    private MapAccessors() {
    }

    private static final MapAccessor EMPTY = new WrapperObjectMap(Collections.emptyMap());

    public static MapAccessor empty() {
        return EMPTY;
    }

    public static MapAccessor wrap(Map<String, ?> map) {
        return new WrapperObjectMap(map);
    }

    public static MapAccessor cast(Object value, MapAccessor defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof MapAccessor) {
            return (MapAccessor) value;
        }
        if (value instanceof Map) {
            return MapAccessors.wrap(as(value));
        }
        throw new ClassCastException(format("{} can not cast {}", value.getClass(), MapAccessor.class));
    }

}
