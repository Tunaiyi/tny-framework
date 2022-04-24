package com.tny.game.data.storage;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 6:14 下午
 */
public interface AsyncObjectStorage<K extends Comparable<?>, O> extends ObjectStorage<K, O> {

    ObjectStorageMonitor getMonitor();

    int queueSize();

    StoreExecuteAction store(int maxSize, int tryTimes);

    void operateAll();

}
