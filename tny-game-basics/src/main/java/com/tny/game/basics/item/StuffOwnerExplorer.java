package com.tny.game.basics.item;

import java.util.Collection;

/**
 * Storage总管理器
 *
 * @author KGTny
 */
public interface StuffOwnerExplorer {

	<O extends StuffOwner<?, ?>> O getOwner(long playerId, int itemId);

	boolean insertStorage(StuffOwner<?, ?>... storageArray);

	<O extends StuffOwner<?, ?>> Collection<O> insertStorage(Collection<O> storageCollection);

	boolean updateStorage(StuffOwner<?, ?>... storageArray);

	<O extends StuffOwner<?, ?>> Collection<O> updateStorage(Collection<O> storageCollection);

	boolean saveStorage(StuffOwner<?, ?>... storageArray);

	<O extends StuffOwner<?, ?>> Collection<O> saveStorage(Collection<O> storageCollection);

	void deleteStorage(StuffOwner<?, ?>... storageArray);

	<O extends StuffOwner<?, ?>> void deleteStorage(Collection<O> storageCollection);

}