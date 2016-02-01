package com.tny.game.base.item;

import java.util.Collection;

/**
 * Owner总管理器
 *
 * @author KGTny
 */
public interface OwnerExplorer {

    <O extends Owner<?, ?>> O getOwner(long playerID, int itemID, Object... object);

    boolean insertOwner(Owner<?, ?>... owners);

    <O extends Owner<?, ?>> Collection<O> insertOwner(Collection<O> ownerCollection);

    boolean updateOwner(Owner<?, ?>... owners);

    <O extends Owner<?, ?>> Collection<O> updateOwner(Collection<O> ownerCollection);

    boolean saveOwner(Owner<?, ?>... owners);

    <O extends Owner<?, ?>> Collection<O> saveOwner(Collection<O> ownerCollection);

    boolean deleteOwner(Owner<?, ?>... owners);

    <O extends Owner<?, ?>> Collection<O> deleteOwner(Collection<O> ownerCollection);

}