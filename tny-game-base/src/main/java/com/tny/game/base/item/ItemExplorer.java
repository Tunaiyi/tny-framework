package com.tny.game.base.item;

import java.util.Collection;

/**
 * 事物模型管理器的管理器
 *
 * @author KGTny
 */
public interface ItemExplorer {

    boolean hasItemManager(ItemType itemType);

    <I extends Entity<?>> I getItem(long playerId, int id, Object... object);

    boolean insertItem(Entity<?>... items);

    <I extends Entity<?>> Collection<I> insertItem(Collection<I> itemCollection);

    boolean updateItem(Entity<?>... items);

    <I extends Entity<?>> Collection<I> updateItem(Collection<I> itemCollection);

    boolean saveItem(Entity<?>... items);

    <I extends Entity<?>> Collection<I> saveItem(Collection<I> itemCollection);

    boolean deleteItem(Entity<?>... items);

    <I extends Entity<?>> Collection<I> deleteItem(Collection<I> itemCollection);

}
