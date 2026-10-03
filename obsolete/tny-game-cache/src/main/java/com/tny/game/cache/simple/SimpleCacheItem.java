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
package com.tny.game.cache.simple;

import com.tny.game.cache.*;

public class SimpleCacheItem<T, R> extends RawCacheItem<R, T> {

    private static final long serialVersionUID = 1L;

    private String key;

    private T data;

    private long version;

    private long expire;

    public SimpleCacheItem() {
    }

    public SimpleCacheItem(String key, T value) {
        this(key, value, 0L, -1L);
    }

    public SimpleCacheItem(String key, T value, long millisecond) {
        this(key, value, 0L, millisecond);
    }

    public SimpleCacheItem(String key, T value, long version, long millisecond) {
        this.key = key;
        this.data = value;
        this.version = version;
        this.expire = millisecond;
    }

    @Override
    public long getExpire() {
        return this.expire;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public T getData() {
        return data;
    }

}
