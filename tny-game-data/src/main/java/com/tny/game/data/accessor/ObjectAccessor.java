package com.tny.game.data.accessor;

import java.util.Collection;
import java.util.Map;

/**
 * @author KGTny
 */
public interface ObjectAccessor<K extends Comparable<K>, O> {

    O get(K key);

    Map<String, ? extends O> get(Collection<? extends K> ids);

    boolean insert(O object);

    Collection<O> insert(Collection<O> objects);

    boolean update(O object);

    Collection<O> update(Collection<O> objects);

    boolean delete(O object);

    Collection<O> delete(Collection<O> objects);

    boolean save(O object);

    Collection<O> save(Collection<O> objects);

}
