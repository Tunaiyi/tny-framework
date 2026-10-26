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

package com.tny.game.common.collection.empty;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.Supplier;

public class EmptyImmutableMap<K, V> implements Map<K, V> {

    private volatile Map<K, V> map = ImmutableMap.of();

    private Supplier<Map<K, V>> creator;

    public EmptyImmutableMap() {
    }

    public EmptyImmutableMap(Supplier<Map<K, V>> creator) {
        this.creator = creator;
    }

    private Map<K, V> getWriter() {
        Map<K, V> current = this.map;
        if (!(current instanceof ImmutableMap)) {
            return current;
        }
        // 首写竞态：双检锁下唯一换表，输家复用胜者新表——并发首写不再各建各表互相覆盖
        synchronized (this) {
            current = this.map;
            if (current instanceof ImmutableMap) {
                current = this.creator != null ? this.creator.get() : new HashMap<>();
                this.map = current;
            }
            return current;
        }
    }

    private Map<K, V> getReader() {
        if (this.map == null) {
            this.map = ImmutableMap.of();
        }
        return this.map;
    }

    @Override
    public int size() {
        return getReader().size();
    }

    @Override
    public boolean isEmpty() {
        return getReader().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getReader().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getReader().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return getReader().get(key);
    }

    @Override
    public V put(K key, V value) {
        if (key != null) {
            return getWriter().put(key, value);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        if (key != null) {
            return getWriter().remove(key);
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (!m.isEmpty()) {
            getWriter().putAll(m);
        }
    }

    @Override
    public void clear() {
        if (!this.getReader().isEmpty()) {
            getWriter().clear();
        }
    }

    @Override
    public Set<K> keySet() {
        return getReader().keySet();
    }

    @Override
    public Collection<V> values() {
        return getReader().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return getReader().entrySet();
    }

}
