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

    /**
     * 内容判等（原按引用致集合去重/查找语义失效）；null 键值正常参与。
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Entry)) {
            return false;
        }
        Entry<?, ?> that = (Entry<?, ?>) other;
        return java.util.Objects.equals(this.key, that.getKey())
               && java.util.Objects.equals(this.value, that.getValue());
    }

    @Override
    public final int hashCode() {
        return java.util.Objects.hashCode(this.key) ^ java.util.Objects.hashCode(this.value);
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }

}
