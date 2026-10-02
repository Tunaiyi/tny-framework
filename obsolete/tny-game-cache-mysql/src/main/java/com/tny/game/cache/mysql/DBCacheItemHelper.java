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
package com.tny.game.cache.mysql;

import com.tny.game.cache.*;

import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;

class DBCacheItemHelper {

    @SuppressWarnings("unchecked")
    static List<DBCacheItem<?>> cacheItem2Item(Collection<? extends CacheItem<?>> cacheItems) {
        if (cacheItems.isEmpty()) {
            return Collections.emptyList();
        }
        List<DBCacheItem<?>> items = new ArrayList<>();
        for (CacheItem<?> item : cacheItems) {
            if (item instanceof DBCacheItem) {
                items.add((DBCacheItem<Object>) item);
            } else {
                items.add(new DBCacheItem<>(item));
            }
        }
        return items;
    }

    static <T> List<DBCacheItem<T>> map2Item(Map<String, T> valueMap, long millisecond) {
        if (valueMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<DBCacheItem<T>> items = new ArrayList<>();
        for (Entry<String, T> item : valueMap.entrySet()) {
            items.add(new DBCacheItem<>(item.getKey(), item.getValue(), 0L, millisecond));
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    static <C extends CacheItem<?>> List<C> checkResult(List<? extends DBCacheItem<?>> items, int[] results) {
        List<C> fails = null;
        for (int index = 0; index < results.length; index++) {
            if (results[index] <= 0 && results[index] != Statement.SUCCESS_NO_INFO) {
                DBCacheItem<?> item = items.get(index);
                fails = CacheItemHelper.getAndCreate(fails);
                fails.add((C) item);
            }
        }
        return CacheItemHelper.checkEmpty(fails);
    }

}
