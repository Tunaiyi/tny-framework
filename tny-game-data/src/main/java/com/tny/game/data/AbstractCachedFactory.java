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
