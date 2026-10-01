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
