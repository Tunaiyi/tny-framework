package com.tny.game.data.storage;

/**
 * <p>
 */
public interface ObjectStorage<K extends Comparable<K>, O> {

    void start();

    void stop();

    O get(K id);

    boolean insert(K id, O object);

    boolean update(K id, O object);

    boolean delete(K id, O object);

    boolean save(K id, O object);

    long getInsertNumber();

    long getUpdateNumber();

    long getSaveNumber();

    long getDeleteNumber();

    long getFailedNumber();
}
