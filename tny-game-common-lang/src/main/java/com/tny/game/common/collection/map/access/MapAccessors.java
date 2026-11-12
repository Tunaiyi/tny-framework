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
