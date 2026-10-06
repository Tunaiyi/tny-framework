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

package com.tny.game.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/30 2:26 下午
 */
public class AbstractCachedFactory<K, O> {

    private final Map<K, O> cached = new ConcurrentHashMap<>();

    protected <T extends O> T loadOrCreate(K key, Function<K, O> creator) {
        O object = cached.get(key);
        if (object != null) {
            return as(object);
        }
        synchronized (this) {
            object = cached.get(key);
            if (object != null) {
                return as(object);
            }
            O value = creator.apply(key);
            cached.put(key, value);
            return as(value);
        }
    }

}
