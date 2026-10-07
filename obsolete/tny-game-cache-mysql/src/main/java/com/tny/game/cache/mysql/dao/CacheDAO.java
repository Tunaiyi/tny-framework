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
package com.tny.game.cache.mysql.dao;

import com.tny.game.cache.mysql.*;

import java.util.*;

public interface CacheDAO {

    DBCacheItem get(String key);

    Collection<DBCacheItem> get(Collection<String> keys);

    int add(DBCacheItem item);

    int[] add(Collection<? extends DBCacheItem> items);

    int set(DBCacheItem item);

    int[] set(Collection<? extends DBCacheItem> items);

    int update(DBCacheItem item);

    int[] update(Collection<? extends DBCacheItem> items);

    int cas(DBCacheItem item);

    int cas(Collection<? extends DBCacheItem> items);

    int delete(String key);

    int[] delete(Collection<String> keys);

    void flushAll(Object hash);

    List<String> getAllKeys(Object hash);

    List<String> getKeys(long uid, Object hash);

}
