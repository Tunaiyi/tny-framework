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

package com.tny.game.common.collection;

import java.util.Iterator;
import java.util.function.Function;

public class IterableAide {

    private static final Iterator<Object> EMPTY_ITERATOR = new Iterator<Object>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public void remove() {
        }

    };

    private static final Iterable<?> EMPTY = (Iterable<Object>) () -> EMPTY_ITERATOR;

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> empty() {
        return (Iterable<T>) EMPTY;
    }

    public static <O, N> Iterable<N> trans(Iterable<O> iterable, Function<O, N> fn) {
        return new ConvertIterable<>(iterable, fn);
    }

    private static class ConvertIterator<O, N> implements Iterator<N> {

        private Iterator<O> origIterator;

        private Function<O, N> fn;

        private ConvertIterator(Iterable<O> origIterable, Function<O, N> fn) {
            super();
            this.origIterator = origIterable.iterator();
            this.fn = fn;
        }

        @Override
        public boolean hasNext() {
            return this.origIterator.hasNext();
        }

        @Override
        public N next() {
            O value = this.origIterator.next();
            if (value != null) {
                return this.fn.apply(value);
            }
            return null;
        }

        @Override
        public void remove() {
            this.origIterator.remove();
        }

    }

    private static class ConvertIterable<O, N> implements Iterable<N> {

        private Iterable<O> origIterable;

        private Function<O, N> fn;

        private ConvertIterable(Iterable<O> origIterable, Function<O, N> fn) {
            super();
            this.origIterable = origIterable;
            this.fn = fn;
        }

        @Override
        public Iterator<N> iterator() {
            return new ConvertIterator<>(this.origIterable, this.fn);
        }

    }

    ;
}
