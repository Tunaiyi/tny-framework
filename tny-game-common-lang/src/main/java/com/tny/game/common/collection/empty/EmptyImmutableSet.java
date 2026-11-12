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

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.function.Supplier;

public class EmptyImmutableSet<V> implements Set<V> {

    private Set<V> set = ImmutableSet.of();

    private Supplier<Set<V>> creator;

    public EmptyImmutableSet() {
    }

    public EmptyImmutableSet(Supplier<Set<V>> creator) {
        this.creator = creator;
    }

    private Set<V> getWriter() {
        Set<V> current = this.set;
        if (!(current instanceof ImmutableSet)) {
            return current;
        }
        // 首写竞态：双检锁唯一换表（与 EmptyImmutableMap 模式对齐）
        synchronized (this) {
            current = this.set;
            if (current instanceof ImmutableSet) {
                current = this.creator != null ? this.creator.get() : new HashSet<>();
                this.set = current;
            }
            return current;
        }
    }

    private Set<V> getReader() {
        if (this.set == null) {
            this.set = ImmutableSet.of();
        }
        return this.set;
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
    public boolean contains(Object o) {
        return getReader().contains(o);
    }

    @Override
    public Iterator<V> iterator() {
        return getReader().iterator();
    }

    @Override
    public Object[] toArray() {
        return getReader().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getReader().toArray(a);
    }

    @Override
    public boolean add(V v) {
        return getWriter().add(v);
    }

    @Override
    public boolean remove(Object o) {
        if (o != null) {
            return getWriter().remove(o);
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getReader().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return c.isEmpty() || getWriter().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getWriter().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getWriter().retainAll(c);
    }

    @Override
    public void clear() {
        if (!this.getReader().isEmpty()) {
            getWriter().clear();
        }
    }

}
