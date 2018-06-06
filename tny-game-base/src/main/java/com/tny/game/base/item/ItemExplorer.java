package com.tny.game.base.item;

import java.util.Collection;

/**
 * 事物模型管理器的管理器
 *
 * @author KGTny
 */
public interface ItemExplorer {

    boolean hasItemManager(ItemType itemType);

    <I extends Any<?>> I getItem(long playerID, int id, Object... object);

    boolean insertItem(Any<?>... items);

    <I extends Any<?>> Collection<I> insertItem(Collection<I> itemCollection);

    boolean updateItem(Any<?>... items);

    <I extends Any<?>> Collection<I> updateItem(Collection<I> itemCollection);

    boolean saveItem(Any<?>... items);

    <I extends Any<?>> Collection<I> saveItem(Collection<I> itemCollection);

    boolean deleteItem(Any<?>... items);

    <I extends Any<?>> Collection<I> deleteItem(Collection<I> itemCollection);

}
