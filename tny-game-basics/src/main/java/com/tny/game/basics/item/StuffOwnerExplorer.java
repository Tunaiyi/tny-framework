package com.tny.game.basics.item;

import java.util.Collection;

/**
 * Storage总管理器
 *
 * @author KGTny
 */
public interface StuffOwnerExplorer {

    <O extends StuffOwner<?, ?>> O getOwner(long playerId, ItemType ownerType);

    boolean insertOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> insertOwner(Collection<O> storageCollection);

    boolean updateOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> updateOwner(Collection<O> storageCollection);

    boolean saveOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> Collection<O> saveOwner(Collection<O> storageCollection);

    void deleteOwner(StuffOwner<?, ?>... storageArray);

    <O extends StuffOwner<?, ?>> void deleteOwner(Collection<O> storageCollection);

}