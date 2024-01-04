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

package com.tny.game.common.concurrent.collection;

import java.io.Serializable;
import java.util.Map.Entry;

public class ImmutableEntry<K, V> implements Entry<K, V>, Serializable {

    private static final long serialVersionUID = 1L;

    private final K key;

    private final V value;

    public static <K, V> ImmutableEntry<K, V> entry(K key, V value) {
        return new ImmutableEntry<>(key, value);
    }

    private ImmutableEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public final K getKey() {
        return this.key;
    }

    @Override
    public final V getValue() {
        return this.value;
    }

    @Override
    public final V setValue(V value) {
        throw new UnsupportedOperationException();
    }

}
