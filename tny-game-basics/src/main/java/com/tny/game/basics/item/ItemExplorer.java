package com.tny.game.basics.item;

import java.util.Collection;

/**
 * 事物模型管理器的管理器
 *
 * @author KGTny
 */
public interface ItemExplorer {

	boolean hasItemManager(ItemType itemType);

	<I extends Entity<?>> I getItem(long playerId, int modelId);

	boolean insertItem(Entity<?>... items);

	<I extends Entity<?>> Collection<I> insertItem(Collection<I> itemCollection);

	boolean updateItem(Entity<?>... items);

	<I extends Entity<?>> Collection<I> updateItem(Collection<I> itemCollection);

	boolean saveItem(Entity<?>... items);

	<I extends Entity<?>> Collection<I> saveItem(Collection<I> itemCollection);

	void deleteItem(Entity<?>... items);

	<I extends Entity<?>> void deleteItem(Collection<I> itemCollection);

}
