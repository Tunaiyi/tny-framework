package com.tny.game.basics.item;

import java.util.Collection;

/**
 * 事物模型管理器的管理器
 *
 * @author KGTny
 */
public interface ItemExplorer {

	boolean hasItemManager(ItemType itemType);

	<I extends Subject<?>> I getItem(long playerId, int modelId);

	<I extends Subject<?>> I getItem(AnyId anyId);

	boolean insertItem(Subject<?>... items);

	<I extends Subject<?>> Collection<I> insertItem(Collection<I> itemCollection);

	boolean updateItem(Subject<?>... items);

	<I extends Subject<?>> Collection<I> updateItem(Collection<I> itemCollection);

	boolean saveItem(Subject<?>... items);

	<I extends Subject<?>> Collection<I> saveItem(Collection<I> itemCollection);

	void deleteItem(Subject<?>... items);

	<I extends Subject<?>> void deleteItem(Collection<I> itemCollection);

}
