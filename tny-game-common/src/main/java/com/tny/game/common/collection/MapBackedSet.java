package com.tny.game.common.collection;

import java.io.Serializable;
import java.util.*;

public class MapBackedSet<E> extends AbstractSet<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final Map<E, Boolean> map;

    public MapBackedSet(Map<E, Boolean> map) {
        this.map = map;
    }

    public MapBackedSet(Map<E, Boolean> map, Collection<E> c) {
        this.map = map;
        addAll(c);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }

    @Override
    public boolean add(E o) {
        return this.map.put(o, Boolean.TRUE) == null;
    }

    @Override
    public boolean remove(Object o) {
        return this.map.remove(o) != null;
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}