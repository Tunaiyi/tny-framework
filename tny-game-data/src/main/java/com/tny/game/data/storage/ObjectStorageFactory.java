package com.tny.game.data.storage;

import com.tny.game.data.cache.*;

/**
 * <p>
 */
public interface ObjectStorageFactory {

    <K extends Comparable<?>, O> ObjectStorage<K, O> createStorage(EntityScheme cacheScheme, EntityKeyMaker<K, O> keyMaker);

}
