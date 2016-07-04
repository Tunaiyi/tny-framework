package com.tny.game.common.utils.collection;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

public class EmptyImmutableList<V> implements List<V> {

    private List<V> list = ImmutableList.of();

    private Supplier<List<V>> creator;

    public EmptyImmutableList() {
    }

    public EmptyImmutableList(Supplier<List<V>> creator) {
        this.creator = creator;
    }

    private List<V> getWriter() {
        if (list instanceof ImmutableList)
            this.list = creator != null ? creator.get() : new ArrayList<>();
        return this.list;
    }

    private List<V> getReader() {
        if (list == null)
            this.list = ImmutableList.of();
        return this.list;
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
        if (o != null)
            return getWriter().remove(o);
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
    public boolean addAll(int index, Collection<? extends V> c) {
        return c.isEmpty() || getWriter().addAll(index, c);
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
        if (!this.getReader().isEmpty())
            getWriter().clear();
    }

    @Override
    public V get(int index) {
        return getReader().get(index);
    }

    @Override
    public V set(int index, V element) {
        return getWriter().set(index, element);
    }

    @Override
    public void add(int index, V element) {
        getWriter().add(index, element);
    }

    @Override
    public V remove(int index) {
        return getWriter().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getReader().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getReader().lastIndexOf(o);
    }

    @Override
    public ListIterator<V> listIterator() {
        return getReader().listIterator();
    }

    @Override
    public ListIterator<V> listIterator(int index) {
        return getReader().listIterator(index);
    }

    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        return getReader().subList(fromIndex, toIndex);
    }

}
