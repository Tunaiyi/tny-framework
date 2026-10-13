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

package com.tny.game.data.mongodb.utils;

import org.springframework.data.mongodb.core.query.*;

import java.util.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-03 04:17
 */
public class QueryUtils {

    public static Criteria idIs(Object id) {
        return Criteria.where(MongoUtils.ID).is(id);
    }

    public static <T> Criteria idIn(Collection<T> ids) {
        if (ids.size() == 1) {
            for (T id : ids) {
                return idIs(id);
            }
        }
        return Criteria.where(MongoUtils.ID).in(ids);
    }

    public static Query queryIdIs(Object id) {
        return Query.query(idIs(id));
    }

    public static <T> Query queryIdIn(Collection<T> ids) {
        return Query.query(idIn(ids));
    }

    public static Query queryIdIn(Object... ids) {
        return Query.query(idIn(Arrays.asList(ids)));
    }

    public static void updateIfNotNull(Update update, String field, Object value) {
        if (value != null) {
            update.set(field, value);
        }
    }

}
