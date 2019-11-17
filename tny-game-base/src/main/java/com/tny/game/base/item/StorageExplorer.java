package com.tny.game.base.item;

import java.util.Collection;

/**
 * Storage总管理器
 *
 * @author KGTny
 */
public interface StorageExplorer {

    <O extends Storage<?, ?>> O getStorage(long playerId, int itemID, Object... object);

    boolean insertStorage(Storage<?, ?>... storageArray);

    <O extends Storage<?, ?>> Collection<O> insertStorage(Collection<O> storageCollection);

    boolean updateStorage(Storage<?, ?>... storageArray);

    <O extends Storage<?, ?>> Collection<O> updateStorage(Collection<O> storageCollection);

    boolean saveStorage(Storage<?, ?>... storageArray);

    <O extends Storage<?, ?>> Collection<O> saveStorage(Collection<O> storageCollection);

    boolean deleteStorage(Storage<?, ?>... storageArray);

    <O extends Storage<?, ?>> Collection<O> deleteStorage(Collection<O> storageCollection);

}