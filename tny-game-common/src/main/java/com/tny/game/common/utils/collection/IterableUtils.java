package com.tny.game.common.utils.collection;

import java.util.Iterator;
import java.util.function.Function;


public class IterableUtils {

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

    private static final Iterable<?> EMPTY = new Iterable<Object>() {

        @Override
        public Iterator<Object> iterator() {
            return EMPTY_ITERATOR;
        }

    };

    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> empty() {
        return (Iterable<T>) EMPTY;
    }

    public static <O, N> Iterable<N> trans(Iterable<O> iterable, Function<O, N> fn) {
        return new ConvertIterable<O, N>(iterable, fn);
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
            return origIterator.hasNext();
        }

        @Override
        public N next() {
            O value = origIterator.next();
            if (value != null)
                return fn.apply(value);
            return null;
        }

        @Override
        public void remove() {
            origIterator.remove();
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
            return new ConvertIterator<O, N>(origIterable, fn);
        }

    }

    ;
}
