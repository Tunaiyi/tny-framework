package com.tny.game.common.utils.collection;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashSet<E> extends MapBackedSet<E> {

    private static final long serialVersionUID = 1L;

    public ConcurrentHashSet() {
        super(new ConcurrentHashMap<E, Boolean>());
    }

    public ConcurrentHashSet(Collection<E> c) {
        super(new ConcurrentHashMap<E, Boolean>(), c);
    }

    @Override
    public boolean add(E o) {
        Boolean answer = ((ConcurrentMap<E, Boolean>) map).putIfAbsent(o, Boolean.TRUE);
        return answer == null;
    }
}
