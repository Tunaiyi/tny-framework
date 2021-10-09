package com.tny.game.data.storage;

/**
 * <p>
 */
public interface ObjectStorage<K extends Comparable<?>, O> {

	O get(K id);

	boolean insert(K id, O object);

	boolean update(K id, O object);

	boolean save(K id, O object);

	boolean delete(K key, O object);

}
