package com.tny.game.common.concurrent.collection;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends MapBackedSet<E> {

    private static final long serialVersionUID = 1L;

    public ConcurrentHashSet() {
        super(new ConcurrentHashMap<>());
    }

    public ConcurrentHashSet(Collection<E> c) {
        super(new ConcurrentHashMap<>(), c);
    }

    @Override
    public boolean add(E o) {
        Boolean answer = this.map.putIfAbsent(o, Boolean.TRUE);
        return answer == null;
    }
}
