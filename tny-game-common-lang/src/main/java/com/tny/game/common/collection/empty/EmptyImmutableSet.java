/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
        if (this.set instanceof ImmutableSet) {
            this.set = this.creator != null ? this.creator.get() : new HashSet<>();
        }
        return this.set;
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
